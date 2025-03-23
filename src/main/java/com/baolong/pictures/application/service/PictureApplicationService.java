package com.baolong.pictures.application.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.domain.category.aggregate.Category;
import com.baolong.pictures.domain.picture.aggregate.Picture;
import com.baolong.pictures.domain.picture.aggregate.PictureInteraction;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureInteractionStatusEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureInteractionTypeEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureReviewStatusEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureShareStatusEnum;
import com.baolong.pictures.domain.picture.service.PictureDomainService;
import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.api.grab.model.GrabPictureResult;
import com.baolong.pictures.infrastructure.api.pictureSearch.model.SearchPictureResult;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.manager.message.EmailManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 图片表 (picture) - 应用服务
 */
@Service
@RequiredArgsConstructor
public class PictureApplicationService {
	// 领域服务
	private final PictureDomainService pictureDomainService;
	// 应用服务
	private final UserApplicationService userApplicationService;
	private final SpaceApplicationService spaceApplicationService;
	private final CategoryApplicationService categoryApplicationService;
	private final EmailManager emailManager;

	private final TransactionTemplate transactionTemplate;

	/**
	 * 上传图片
	 *
	 * @param pictureInputSource 图片输入源
	 * @param picture            图片领域对象
	 * @return 图片领域对象
	 */
	public Picture uploadPicture(Object pictureInputSource, Picture picture) {
		User loginUser = userApplicationService.getLoginUserDetail();
		Long userId = loginUser.getUserId();

		// 存在图片ID说明是编辑操作重新上传图片
		Long pictureId = picture.getPictureId();
		if (ObjUtil.isNotEmpty(pictureId)) {
			// 判断是否有图片的编辑权限
			pictureDomainService.canOperateInPicture(pictureId, userId, loginUser.isAdmin());
		}

		// 存在空间ID说明是上传到空间
		Long spaceId = picture.getSpaceId();
		if (ObjUtil.isNotEmpty(spaceId) && !spaceId.equals(0L)) {
			// 判断当前用户是否有空间操作权限
			spaceApplicationService.canOperateInSpace(spaceId, userId, loginUser.isAdmin());
			// 判断当前用户的空间是否还有额度
			spaceApplicationService.canCapacityInSpace(userId, loginUser.isAdmin());
			picture.setSpaceId(spaceId);
		}

		// 填充参数
		picture.setUserId(userId);
		// 填充审核参数
		picture.fillReviewParams(loginUser.isAdmin(), userId, spaceId);

		// 开启事务执行数据库操作
		return transactionTemplate.execute(status -> {
			// 先把旧的图片信息查询出来
			Picture oldPicture = pictureDomainService.getPictureByPictureId(pictureId);
			// 执行上传操作, 里面已经做了上传成功后保存数据库的操作
			Picture newPicture = pictureDomainService.uploadPicture(
					pictureInputSource == null ? picture.getPictureUrl() : pictureInputSource, picture
			);
			long size = picture.getOriginSize();
			long count = 1;
			// 存在旧图片说明是编辑操作重新上传图片
			if (oldPicture != null) {
				// 清除存储服务器中的旧图片
				pictureDomainService.clearPictureFile(oldPicture);
				if (ObjectUtil.isNotEmpty(spaceId) && !spaceId.equals(0L)) {
					size -= oldPicture.getOriginSize();
					count -= 1L;
				}
			}
			// 更新空间额度
			if (ObjectUtil.isNotEmpty(spaceId) && !spaceId.equals(0L)) {
				spaceApplicationService.updateSpaceSizeAndCount(spaceId, size, count);
			}
			return newPicture;
		});
	}

	/**
	 * 删除图片
	 *
	 * @param pictureId 图片ID
	 */
	public void deletePicture(Long pictureId) {
		User loginUser = userApplicationService.getLoginUserDetail();
		Long userId = loginUser.getUserId();
		// 判断是否有图片的编辑权限
		pictureDomainService.canOperateInPicture(pictureId, userId, loginUser.isAdmin());
		// 查询是否存在
		Picture picture = pictureDomainService.getPictureByPictureId(pictureId);
		// 存在空间ID说明是空间相关的图片
		Long spaceId = picture.getSpaceId();
		if (ObjUtil.isNotEmpty(spaceId) && !spaceId.equals(0L)) {
			// 判断当前用户是否有空间操作权限
			spaceApplicationService.canOperateInSpace(spaceId, userId, loginUser.isAdmin());
		}

		// 开启事务执行数据库操作
		transactionTemplate.execute(status -> {
			pictureDomainService.deletePicture(pictureId);
			if (picture.getSpaceId() != null && !picture.getSpaceId().equals(0L)) {
				// 更新空间额度
				spaceApplicationService.updateSpaceSizeAndCount(picture.getSpaceId(), -picture.getOriginSize(), -1L);
			}
			// 删除存储服务器中的旧图片
			pictureDomainService.clearPictureFile(picture);
			return true;
		});
	}

	/**
	 * 编辑图片
	 *
	 * @param picture 图片领域对象
	 */
	@Transactional(rollbackFor = Exception.class)
	public void editPicture(Picture picture) {
		User loginUser = userApplicationService.getLoginUserDetail();
		Long userId = loginUser.getUserId();
		Long pictureId = picture.getPictureId();
		// 判断图片是否存在
		pictureDomainService.existedPictureByPictureId(pictureId);
		// 判断是否有图片的编辑权限
		pictureDomainService.canOperateInPicture(pictureId, userId, loginUser.isAdmin());
		// 存在空间ID说明是空间相关的图片
		Long spaceId = picture.getSpaceId();
		if (ObjUtil.isNotEmpty(spaceId) && !spaceId.equals(0L)) {
			// 判断当前用户是否有空间操作权限
			spaceApplicationService.canOperateInSpace(spaceId, userId, loginUser.isAdmin());
		}
		// 填充审核参数
		picture.fillReviewParams(loginUser.isAdmin(), userId, spaceId);
		// 操作数据库
		pictureDomainService.editPicture(picture);
	}

	/**
	 * 图片下载
	 *
	 * @param pictureId 图片 ID
	 * @return 原图地址
	 */
	public String pictureDownload(Long pictureId) {
		Picture picture = pictureDomainService.getPictureByPictureId(pictureId);
		// 更新图片操作类型数量
		pictureDomainService.updateInteractionNum(pictureId, PictureInteractionTypeEnum.DOWNLOAD.getKey(), 1);
		return picture.getOriginUrl();
	}

	/**
	 * 图片分享
	 *
	 * @param pictureId 图片 ID
	 */
	public void pictureShare(Long pictureId) {
		Picture picture = pictureDomainService.getPictureByPictureId(pictureId);
		if (!PictureShareStatusEnum.isShare(picture.getIsShare())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前图片作者不允许分享!");
		}
		// 更新图片操作类型数量
		pictureDomainService.updateInteractionNum(pictureId, PictureInteractionTypeEnum.SHARE.getKey(), 1);
	}

	/**
	 * 图片点赞或收藏
	 *
	 * @param pictureInteraction 图片互动领域对象
	 */
	public void pictureLikeOrCollect(PictureInteraction pictureInteraction) {
		Long pictureId = pictureInteraction.getPictureId();
		pictureDomainService.existedPictureByPictureId(pictureId);
		pictureDomainService.changePictureLikeOrCollect(pictureInteraction);
		Integer interactionType = pictureInteraction.getInteractionType();
		// 更新互动类型数量
		pictureDomainService.updateInteractionNum(pictureId, interactionType,
				PictureInteractionStatusEnum.CANCEL.getKey().equals(pictureInteraction.getInteractionStatus()) ?
						-1 : 1);
	}

	/**
	 * 审核图片
	 *
	 * @param pictureList 图片领域对象列表
	 */
	@Transactional(rollbackFor = Exception.class)
	public void reviewPicture(List<Picture> pictureList) {
		pictureDomainService.reviewPicture(pictureList, StpUtil.getLoginIdAsLong());

		// 审核通知
		Set<Long> pictureIds = pictureList.stream().map(Picture::getPictureId).collect(Collectors.toSet());
		List<Picture> pictures = this.getPictureByPictureIds(pictureIds);
		Set<Long> userIds = pictures.stream().map(Picture::getUserId).collect(Collectors.toSet());
		List<User> userList = userApplicationService.getUserListByUserIds(userIds);
		List<String> emailList = userList.stream().map(User::getUserEmail).collect(Collectors.toList());
		emailManager.sendEmailAsReview(emailList, "图片审核通知", PictureReviewStatusEnum.PASS.getKey().equals(pictureList.get(0).getReviewStatus())
						? "审核通过" : "审核不通过");
	}

	/**
	 * 根据图片ID获取图片
	 *
	 * @param pictureId 图片ID
	 * @return 图片
	 */
	public Picture getPictureDetailById(Long pictureId) {
		Picture picture = pictureDomainService.getPictureByPictureId(pictureId);
		// 查询图片的用户信息
		User user = userApplicationService.getUserDetailById(picture.getUserId());
		picture.setUserName(user.getUserName());
		picture.setUserAvatar(user.getUserAvatar());
		// 查询分类信息
		if (picture.getCategoryId() != null) {
			Category category = categoryApplicationService.getCategoryByCategoryId(picture.getCategoryId());
			if (category != null) {
				picture.setCategoryName(category.getName());
			}
		}
		// 查询空间信息
		if (picture.getSpaceId() != null && !picture.getSpaceId().equals(0L)) {
			Space space = spaceApplicationService.getSpaceBySpaceId(picture.getSpaceId());
			picture.setSpaceName(space.getSpaceName());
			picture.setSpaceType(space.getSpaceType());
		}
		// 处理标签信息
		if (StrUtil.isNotEmpty(picture.getTags())) {
			picture.setTagList(Arrays.asList(picture.getTags().split(",")));
		}
		// 如果当前是登录状态, 查询当前登录用户对该图片的点赞和收藏信息
		if (StpUtil.isLogin()) {
			List<PictureInteraction> pictureInteractions = pictureDomainService.getPictureInteractionByPictureIdAndUserId(
					pictureId, StpUtil.getLoginIdAsLong());
			if (CollUtil.isNotEmpty(pictureInteractions)) {
				for (PictureInteraction pictureInteraction : pictureInteractions) {
					if (pictureInteraction.getInteractionType().equals(PictureInteractionTypeEnum.LIKE.getKey())) {
						picture.setLoginUserIsLike(
								PictureInteractionStatusEnum.isExisted(pictureInteraction.getInteractionStatus()));
					}
					if (pictureInteraction.getInteractionType().equals(PictureInteractionTypeEnum.COLLECT.getKey())) {
						picture.setLoginUserIsCollect(
								PictureInteractionStatusEnum.isExisted(pictureInteraction.getInteractionStatus()));
					}
				}
			}
		}
		// 更新图片操作类型数量
		pictureDomainService.updateInteractionNum(pictureId, PictureInteractionTypeEnum.VIEW.getKey(), 1);
		return picture;
	}

	/**
	 * 根据图片ID集合获取图片列表
	 *
	 * @param pictureIds 图片ID集合
	 * @return 图片列表
	 */
	private List<Picture> getPictureByPictureIds(Set<Long> pictureIds) {
		return pictureDomainService.getPictureByPictureIds(pictureIds);
	}

	/**
	 * 获取首页图片列表
	 *
	 * @param picture 图片领域对象
	 * @return 首页图片列表
	 */
	public PageVO<Picture> getPicturePageListAsHome(Picture picture) {
		PageVO<Picture> picturePageVO = pictureDomainService.getPicturePageListAsHome(picture);
		if (CollUtil.isNotEmpty(picturePageVO.getRecords())) {
			List<Picture> pictureList = picturePageVO.getRecords();
			System.out.println(JSONUtil.parse(pictureList));
			// 查询图片的用户信息
			Set<Long> userIds = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
			List<User> userList = userApplicationService.getUserListByUserIds(userIds);
			Map<Long, List<User>> userListMap = userList
					.stream().collect(Collectors.groupingBy(User::getUserId));
			// 查询当前登录用户对该图片的 点赞和收藏 信息
			Map<Long, Boolean> likeMap = new HashMap<>();
			Map<Long, Boolean> collectMap = new HashMap<>();
			if (StpUtil.isLogin()) {
				Set<Long> pictureIds = pictureList.stream().map(Picture::getPictureId).collect(Collectors.toSet());
				List<PictureInteraction> pictureInteractions = pictureDomainService
						.getPictureInteractionByPictureIdsAndUserId(pictureIds, StpUtil.getLoginIdAsLong());
				if (CollUtil.isNotEmpty(pictureInteractions)) {
					pictureInteractions.forEach(pi -> {
						if (PictureInteractionTypeEnum.LIKE.getKey().equals(pi.getInteractionType()) &&
								PictureInteractionStatusEnum.isExisted(pi.getInteractionStatus())) {
							likeMap.put(pi.getPictureId(), true);
						}
						if (PictureInteractionTypeEnum.COLLECT.getKey().equals(pi.getInteractionType()) &&
								PictureInteractionStatusEnum.isExisted(pi.getInteractionStatus())) {
							collectMap.put(pi.getPictureId(), true);
						}
					});
				}
			}
			pictureList.forEach(p -> {
				// 设置作者信息
				Long userId = p.getUserId();
				if (userListMap.containsKey(userId)) {
					p.setUserName(userListMap.get(userId).get(0).getUserName());
					p.setUserAvatar(userListMap.get(userId).get(0).getUserAvatar());
				}
				// 设置当前登录用户点赞和收藏信息
				p.setLoginUserIsLike(likeMap.getOrDefault(p.getPictureId(), false));
				p.setLoginUserIsCollect(collectMap.getOrDefault(p.getPictureId(), false));
			});
		}
		return picturePageVO;
	}

	/**
	 * 获取个人空间图片分页列表
	 *
	 * @param picture 图片领域对象
	 * @return 个人空间图片分页列表
	 */
	public PageVO<Picture> getPicturePageListAsPersonSpace(Picture picture) {
		// 查获取当前登录用户的空间信息
		Space space = spaceApplicationService.getSpaceDetailByLoginUser();
		picture.setSpaceId(space.getSpaceId());
		PageVO<Picture> picturePageVO = pictureDomainService.getPicturePageListAsPersonSpace(picture);
		List<Picture> pictureList = picturePageVO.getRecords();
		if (CollUtil.isNotEmpty(pictureList)) {
			// 查询分类信息
			Set<Long> categoryIds = pictureList.stream().map(Picture::getCategoryId).collect(Collectors.toSet());
			Map<Long, List<Category>> categoryListMap = categoryApplicationService.getCategoryListByCategoryIds(categoryIds)
					.stream().collect(Collectors.groupingBy(Category::getCategoryId));
			pictureList.forEach(p -> {
				// 设置分类信息
				Long categoryId = p.getCategoryId();
				if (categoryListMap.containsKey(categoryId)) {
					p.setCategoryInfo(categoryListMap.get(categoryId).get(0));
				}
			});
		}
		return picturePageVO;
	}

	/**
	 * 获取个人发布的图片分页列表
	 *
	 * @param picture 图片领域对象
	 * @return 个人发布的图片分页列表
	 */
	public PageVO<Picture> getPicturePageListAsPersonRelease(Picture picture) {
		PageVO<Picture> picturePageVO = pictureDomainService.getPicturePageListAsPersonRelease(picture);
		List<Picture> pictureList = picturePageVO.getRecords();
		if (CollUtil.isNotEmpty(pictureList)) {
			// 查询分类信息
			Set<Long> categoryIds = pictureList.stream().map(Picture::getCategoryId).collect(Collectors.toSet());
			Map<Long, List<Category>> categoryListMap = categoryApplicationService.getCategoryListByCategoryIds(categoryIds)
					.stream().collect(Collectors.groupingBy(Category::getCategoryId));
			pictureList.forEach(p -> {
				// 设置分类信息
				Long categoryId = p.getCategoryId();
				if (categoryListMap.containsKey(categoryId)) {
					p.setCategoryInfo(categoryListMap.get(categoryId).get(0));
				}
			});
		}
		return picturePageVO;
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param picture 图片领域对象
	 * @return 图片管理分页列表
	 */
	public PageVO<Picture> getPicturePageListAsManage(Picture picture) {
		PageVO<Picture> picturePageVO = pictureDomainService.getPicturePageListAsManage(picture);
		List<Picture> pictureList = picturePageVO.getRecords();
		if (CollUtil.isNotEmpty(pictureList)) {
			// 查询图片的用户信息
			Set<Long> userIds = pictureList.stream().map(Picture::getUserId).collect(Collectors.toSet());
			Map<Long, List<User>> userListMap = userApplicationService.getUserListByUserIds(userIds)
					.stream().collect(Collectors.groupingBy(User::getUserId));
			// 查询分类信息
			Set<Long> categoryIds = pictureList.stream().map(Picture::getCategoryId).collect(Collectors.toSet());
			Map<Long, List<Category>> categoryListMap = categoryApplicationService.getCategoryListByCategoryIds(categoryIds)
					.stream().collect(Collectors.groupingBy(Category::getCategoryId));
			pictureList.forEach(p -> {
				Long userId = picture.getUserId();
				if (userListMap.containsKey(userId)) {
					p.setUserInfo(userListMap.get(userId).get(0));
				}
				// 设置分类信息
				Long categoryId = picture.getCategoryId();
				if (categoryListMap.containsKey(categoryId)) {
					p.setCategoryInfo(categoryListMap.get(categoryId).get(0));
				}
			});
		}
		return picturePageVO;
	}

	/**
	 * 爬取图片
	 *
	 * @param picture 图片领域对象
	 * @return 爬取的图片列表
	 */
	public List<GrabPictureResult> grabPicture(Picture picture) {
		return pictureDomainService.grabPicture(picture);
	}

	/**
	 * 上传爬取图片
	 *
	 * @param picture 图片领域对象
	 */
	public void uploadPictureByGrab(Picture picture) {
		pictureDomainService.uploadPictureByGrab(picture);
	}

	/**
	 * 以图搜图
	 *
	 * @param picture 图片领域对象
	 * @return 搜图的图片列表
	 */
	public List<SearchPictureResult> searchPicture(Picture picture) {
		return pictureDomainService.searchPicture(picture);
	}
}
