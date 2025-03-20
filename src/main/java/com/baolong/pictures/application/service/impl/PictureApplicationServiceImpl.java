package com.baolong.pictures.application.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.application.service.CategoryApplicationService;
import com.baolong.pictures.application.service.PictureApplicationService;
import com.baolong.pictures.application.service.SpaceApplicationService;
import com.baolong.pictures.domain.category.entity.Category;
import com.baolong.pictures.domain.picture.entity.Picture;
import com.baolong.pictures.domain.picture.entity.PictureInteraction;
import com.baolong.pictures.domain.picture.enums.PictureInteractionStatusEnum;
import com.baolong.pictures.domain.picture.enums.PictureInteractionTypeEnum;
import com.baolong.pictures.domain.picture.enums.PictureShareStatusEnum;
import com.baolong.pictures.domain.picture.service.PictureDomainService;
import com.baolong.pictures.domain.picture.service.PictureInteractionDomainService;
import com.baolong.pictures.domain.space.entity.Space;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.api.cos.CosManager;
import com.baolong.pictures.infrastructure.api.grab.model.GrabPictureResult;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.interfaces.web.assembler.PictureAssembler;
import com.baolong.pictures.interfaces.web.request.picture.PictureBatchEditRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureEditRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureGrabRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureInteractionRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureQueryRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureReviewRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureUpdateRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureUploadRequest;
import com.baolong.pictures.interfaces.web.response.picture.PictureDetailVO;
import com.baolong.pictures.interfaces.web.response.picture.PictureHomeVO;
import com.baolong.pictures.interfaces.web.response.picture.PictureVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 图片应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class PictureApplicationServiceImpl implements PictureApplicationService {

	private final PictureDomainService pictureDomainService;
	private final UserApplicationService userApplicationService;
	private final SpaceApplicationService spaceApplicationService;
	private final CategoryApplicationService categoryApplicationService;
	private final PictureInteractionDomainService pictureInteractionDomainService;


	@Resource
	private CosManager cosManager;
	@Resource
	private TransactionTemplate transactionTemplate;

	// region 其他方法

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@Override
	public LambdaQueryWrapper<Picture> getLambdaQueryWrapper(PictureQueryRequest pictureQueryRequest) {
		ThrowUtils.throwIf(pictureQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
		return pictureDomainService.getLambdaQueryWrapper(pictureQueryRequest);
	}

	/**
	 * 校验并填充审核参数
	 *
	 * @param picture   图片对象
	 * @param loginUser 登录用户
	 */
	@Override
	public void checkAndFillReviewParams(Picture picture, User loginUser) {
		pictureDomainService.checkAndFillReviewParams(picture, loginUser);
	}

	/**
	 * 校验图片操作权限
	 *
	 * @param picture   图片对象
	 * @param loginUser 登录用户
	 */
	@Override
	public void checkPictureChangeAuth(Picture picture, User loginUser) {
		pictureDomainService.checkPictureChangeAuth(picture, loginUser);
	}

	/**
	 * 填充图片名称规则
	 *
	 * @param pictureList 图片列表
	 * @param nameRule    名称规则
	 */
	@Override
	public void fillPictureNameRuleBatch(List<Picture> pictureList, String nameRule) {
		pictureDomainService.fillPictureNameRuleBatch(pictureList, nameRule);
	}

	// endregion 其他方法

	// region 增删改相关（包含上传图片）

	/**
	 * 上传图片
	 *
	 * @param pictureInputSource   图片输入源
	 * @param pictureUploadRequest 图片上传请求
	 * @return PictureVO
	 */
	@Override
	public PictureDetailVO uploadPicture(Object pictureInputSource, PictureUploadRequest pictureUploadRequest) {
		Picture picture = PictureAssembler.toPictureEntity(pictureUploadRequest);
		User loginUser = userApplicationService.getLoginUserDetail();
		Long userId = loginUser.getId();
		picture.setUserId(userId);

		Picture oldPicture;
		// 图片 ID 有值则是更新, 需要判断权限
		if (picture.getId() != null) {
			// 查询旧的图片是否存在
			oldPicture = pictureDomainService.getPictureById(picture.getId());
			ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
			// 校验图片操作权限
			this.checkPictureChangeAuth(oldPicture, loginUser);
		} else {
			oldPicture = null;
		}

		// 跟空间相关, 则需要校验空间权限
		Long spaceId = pictureUploadRequest.getSpaceId();
		if (ObjectUtil.isNotEmpty(spaceId) && !spaceId.equals(0L)) {
			spaceApplicationService.checkSpaceUploadAuth(spaceId, loginUser);
			picture.setSpaceId(spaceId);
		}

		// 校验并填充审核参数
		this.checkAndFillReviewParams(picture, loginUser);

		// 开启事务执行数据库操作
		Picture resultPicture = transactionTemplate.execute(status -> {
			Boolean result = pictureDomainService.uploadPicture(
					pictureInputSource == null ? pictureUploadRequest.getPictureUrl() : pictureInputSource, picture
			);
			ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "图片上传失败");
			if (oldPicture != null) {
				// 删除存储服务器中的旧图片
				pictureDomainService.clearPictureFile(oldPicture);
				// 更新空间额度
				Boolean updated = spaceApplicationService.updateSpaceSizeAndCount(spaceId, -oldPicture.getOriginSize(), -1L);
				ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "空间额度更新失败");
			}
			if (ObjectUtil.isNotEmpty(spaceId) && !spaceId.equals(0L)) {
				Boolean updated = spaceApplicationService.updateSpaceSizeAndCount(spaceId, picture.getOriginSize(), 1L);
				ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "空间额度更新失败");
			}
			return picture;
		});

		return PictureAssembler.toPictureDetailVO(resultPicture);
	}

	/**
	 * 删除图片
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	@Override
	public Boolean deletePicture(DeleteRequest deleteRequest) {
		// 查询是否存在
		Picture picture = pictureDomainService.getPictureById(deleteRequest.getId());
		ThrowUtils.throwIf(picture == null, ErrorCode.PARAMS_ERROR, "图片不存在");
		User loginUser = userApplicationService.getLoginUserDetail();
		// 校验图片操作权限
		this.checkPictureChangeAuth(picture, loginUser);
		// 跟空间相关, 则需要校验空间权限
		spaceApplicationService.checkSpaceUploadAuth(picture.getSpaceId(), loginUser);

		// 开启事务执行数据库操作
		transactionTemplate.execute(status -> {
			// 操作数据库
			Boolean result = pictureDomainService.deletePicture(deleteRequest.getId());
			ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
			if (picture.getSpaceId() != null && !picture.getSpaceId().equals(0L)) {
				// 更新空间额度
				Boolean updated = spaceApplicationService.updateSpaceSizeAndCount(picture.getSpaceId(), -picture.getOriginSize(), -1L);
				ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "空间额度更新失败");
			}
			// 删除存储服务器中的旧图片
			pictureDomainService.clearPictureFile(picture);
			return true;
		});
		return true;
	}

	/**
	 * 更新图片
	 *
	 * @param pictureUpdateRequest 图片更新请求
	 * @return 是否成功
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean updatePicture(PictureUpdateRequest pictureUpdateRequest) {
		Picture picture = PictureAssembler.toPictureEntity(pictureUpdateRequest);
		// 数据校验
		picture.validPictureUpdateAndEdit();
		// 判断图片是否存在
		Boolean existed = pictureDomainService.existPictureById(pictureUpdateRequest.getId());
		ThrowUtils.throwIf(!existed, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
		User loginUser = userApplicationService.getLoginUserDetail();
		// 校验图片操作权限
		this.checkPictureChangeAuth(picture, loginUser);
		// 校验并填充审核参数
		this.checkAndFillReviewParams(picture, loginUser);
		/* TODO 标签的处理没完成
		    // 使用分类标签表的方式
		List<String> inputTagList = pictureUpdateRequest.getTags();
		if (pictureUpdateRequest.getInputTagList() != null && !pictureUpdateRequest.getInputTagList().isEmpty()) {
			// 需要把这个里面的标签新增到数据库中
			for (String tag : pictureUpdateRequest.getInputTagList()) {
				CategoryTag categoryTag = new CategoryTag();
				categoryTag.setName(tag);
				categoryTag.setType(CategoryTagEnum.TAG.getValue());
				categoryTag.setUserId(loginTUser.getId());
				categoryTagApplicationService.addCategoryTag(categoryTag);
				// 把新增的id放到 inputTagList 中
				inputTagList.add(String.valueOf(categoryTag.getId()));
			}
		}
		// 把 inputTagList 转为逗号分隔的字符串
		String inputTagListStr = String.join(",", inputTagList);
		tePicture.setTags(inputTagListStr);*/
		Boolean flag = pictureDomainService.updatePicture(picture);
		ThrowUtils.throwIf(!flag, ErrorCode.OPERATION_ERROR, "更新图片失败");
		return true;
	}

	/**
	 * 编辑图片
	 *
	 * @param pictureEditRequest 图片编辑请求
	 * @return 是否成功
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean editPicture(PictureEditRequest pictureEditRequest) {
		Picture picture = PictureAssembler.toPictureEntity(pictureEditRequest);
		// 数据校验
		picture.validPictureUpdateAndEdit();
		// 判断图片是否存在
		Picture oldPicture = pictureDomainService.getPictureById(pictureEditRequest.getId());
		ThrowUtils.throwIf(oldPicture == null, ErrorCode.NOT_FOUND_ERROR, "图片不存在");
		User loginUser = userApplicationService.getLoginUserDetail();
		System.out.println(loginUser);
		// 校验图片操作权限
		this.checkPictureChangeAuth(oldPicture, loginUser);
		// 校验并填充审核参数
		this.checkAndFillReviewParams(picture, loginUser);
		// 跟空间相关, 则需要校验空间权限
		spaceApplicationService.checkSpaceUploadAuth(picture.getSpaceId(), loginUser);
		// 设置编辑时间
		picture.setEditTime(new Date());
		/* TODO 标签的处理没完成
		   List<String> inputTagList = pictureEditRequest.getTags();
		// 使用分类标签表的方式
		if (pictureEditRequest.getInputTagList() != null && !pictureEditRequest.getInputTagList().isEmpty()) {
			// 需要把这个里面的标签新增到数据库中
			for (String tag : pictureEditRequest.getInputTagList()) {
				CategoryTag categoryTag = new CategoryTag();
				categoryTag.setName(tag);
				categoryTag.setType(CategoryTagEnum.TAG.getValue());
				categoryTag.setUserId(loginTUser.getId());
				categoryTagApplicationService.addCategoryTag(categoryTag);
				// 把新增的id放到 inputTagList 中
				inputTagList.add(String.valueOf(categoryTag.getId()));
			}
		}
		// 把 inputTagList 转为逗号分隔的字符串
		String inputTagListStr = String.join(",", inputTagList);
		tePicture.setTags(inputTagListStr);*/
		Boolean flag = pictureDomainService.editPicture(picture);
		ThrowUtils.throwIf(!flag, ErrorCode.OPERATION_ERROR, "编辑图片失败");
		return true;
	}

	/**
	 * 编辑图片（批量）
	 *
	 * @param pictureBatchEditRequest 图片批量编辑请求
	 * @return 是否成功
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean editPictureBatch(PictureBatchEditRequest pictureBatchEditRequest) {
		User loginUser = userApplicationService.getLoginUserDetail();
		// 跟空间相关, 则需要校验空间权限
		Long spaceId = pictureBatchEditRequest.getSpaceId();
		spaceApplicationService.checkSpaceUploadAuth(spaceId, loginUser);
		List<Picture> pictureList = PictureAssembler.toPictureEntityList(pictureBatchEditRequest);
		// 填充图片名称规则
		this.fillPictureNameRuleBatch(pictureList, pictureBatchEditRequest.getNameRule());
		/* TODO 标签未处理
		List<String> inputTagList = pictureBatchEditRequest.getTags();
		// 使用分类标签表的方式
		if (pictureBatchEditRequest.getInputTagList() != null && !pictureBatchEditRequest.getInputTagList().isEmpty()) {
			// 需要把这个里面的标签新增到数据库中
			for (String tag : pictureBatchEditRequest.getInputTagList()) {
				CategoryTag categoryTag = new CategoryTag();
				categoryTag.setName(tag);
				categoryTag.setType(CategoryTagEnum.TAG.getValue());
				categoryTag.setUserId(loginTUser.getId());
				categoryTagApplicationService.addCategoryTag(categoryTag);
				// 把新增的id放到 inputTagList 中
				inputTagList.add(String.valueOf(categoryTag.getId()));
			}
		}
		// 把 inputTagList 转为逗号分隔的字符串
		String inputTagListStr = String.join(",", inputTagList);
		// 遍历更新图片的相关信息
		pictureList.forEach(picture -> {
		});*/
		Boolean flag = pictureDomainService.editPictureBatch(pictureList);
		ThrowUtils.throwIf(!flag, ErrorCode.OPERATION_ERROR, "图片批量编辑失败");
		return false;
	}

	/**
	 * 审核图片
	 *
	 * @param pictureReviewRequest 图片审核请求
	 * @return 是否成功
	 */
	@Transactional(rollbackFor = Exception.class)
	@Override
	public Boolean reviewPicture(PictureReviewRequest pictureReviewRequest) {
		List<Picture> pictureList = PictureAssembler.toPictureEntityList(pictureReviewRequest);
		User loginUser = userApplicationService.getLoginUserDetail();
		pictureList.forEach(picture -> picture.setReviewerUser(loginUser.getId()));
		Boolean flag = pictureDomainService.reviewPicture(pictureList);
		ThrowUtils.throwIf(!flag, ErrorCode.OPERATION_ERROR, "审核图片失败");
		return true;
	}

	// endregion 增删改相关（包含上传图片）

	// region 查询相关

	/**
	 * 根据图片 ID 获取图片信息
	 *
	 * @param pictureId 图片 ID
	 * @return 图片信息
	 */
	@Override
	public Picture getPictureInfoById(Long pictureId) {
		return pictureDomainService.getPictureById(pictureId);
	}

	/**
	 * 根据图片 ID 获取图片详情
	 *
	 * @param pictureId 图片 ID
	 * @return 图片详情
	 */
	@Override
	public PictureDetailVO getPictureDetailById(Long pictureId) {
		Picture picture = pictureDomainService.getPictureById(pictureId);
		if (picture == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在");
		}
		PictureDetailVO pictureDetailVO;
		// 如果当前用户没登录则只获取简单字段即可
		if (!StpUtil.isLogin()) {
			PictureHomeVO PictureHomeVO = PictureAssembler.toPictureSimpleVO(picture);
			pictureDetailVO = PictureAssembler.toPictureDetailVO(PictureHomeVO);
		} else {
			pictureDetailVO = PictureAssembler.toPictureDetailVO(picture);
		}
		// 查询图片的用户信息
		User user = userApplicationService.getUserDetailById(picture.getUserId());
		if (user != null) {
			pictureDetailVO.setUserName(user.getUserName());
			pictureDetailVO.setUserAvatar(user.getUserAvatar());
		} else {
			pictureDetailVO.setUserName("未知用户");
		}
		// 查询分类信息
		if (pictureDetailVO.getCategoryId() != null) {
			Category category = categoryApplicationService.getCategoryInfoById(pictureDetailVO.getCategoryId());
			if (category != null) {
				pictureDetailVO.setCategoryName(category.getName());
			}
		}
		// 查询空间信息
		if (pictureDetailVO.getSpaceId() != null) {
			Space space = spaceApplicationService.getSpaceInfoById(pictureDetailVO.getSpaceId());
			if (space != null) {
				pictureDetailVO.setSpaceName(space.getSpaceName());
				pictureDetailVO.setSpaceType(space.getSpaceType());
			}
		}
		// 标签信息
		if (StrUtil.isNotEmpty(pictureDetailVO.getTags())) {
			pictureDetailVO.setTagList(Arrays.asList(pictureDetailVO.getTags().split(",")));
		}
		// 如果当前是登录状态
		if (StpUtil.isLogin()) {
			// 查询当前登录用户对该图片的 点赞和收藏 信息
			List<PictureInteraction> list = pictureInteractionDomainService.list(new LambdaQueryWrapper<PictureInteraction>()
					.eq(PictureInteraction::getPictureId, pictureId)
					.eq(PictureInteraction::getUserId, StpUtil.getLoginIdAsLong())
			);
			if (CollUtil.isNotEmpty(list)) {
				for (PictureInteraction pictureInteraction : list) {
					if (pictureInteraction.getInteractionType().equals(PictureInteractionTypeEnum.LIKE.getKey())) {
						pictureDetailVO.setLoginUserIsLike(
								PictureInteractionStatusEnum.isExisted(pictureInteraction.getInteractionStatus()));
					}
					if (pictureInteraction.getInteractionType().equals(PictureInteractionTypeEnum.COLLECT.getKey())) {
						pictureDetailVO.setLoginUserIsCollect(
								PictureInteractionStatusEnum.isExisted(pictureInteraction.getInteractionStatus()));
					}
				}
			}
		}
		pictureView(pictureId);
		return pictureDetailVO;
	}

	/**
	 * 获取首页图片列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 首页图片列表
	 */
	@Override
	public PageVO<PictureHomeVO> getPicturePageListAsHome(PictureQueryRequest pictureQueryRequest) {
		pictureQueryRequest.setSpaceId(0L);
		Page<Picture> picturePage = pictureDomainService.getPicturePageListAsHome(
				pictureQueryRequest.getPage(Picture.class), this.getLambdaQueryWrapper(pictureQueryRequest)
		);
		// System.out.println("数据: "+ JSONUtil.parse(picturePage.getRecords()));
		List<PictureHomeVO> simpleVOS = picturePage.getRecords().stream()
				.map(PictureAssembler::toPictureSimpleVO)
				.collect(Collectors.toList());
		if (!simpleVOS.isEmpty()) {
			// 查询图片的用户信息
			Set<Long> userIds = simpleVOS.stream().map(PictureHomeVO::getUserId).collect(Collectors.toSet());
			Map<Long, List<User>> userListMap = userApplicationService.getUserListByUserIds(userIds)
					.stream()
					.collect(Collectors.groupingBy(User::getId));
			// 查询当前登录用户对该图片的 点赞和收藏 信息
			Map<Long, Boolean> likeMap = new HashMap<>();
			Map<Long, Boolean> collectMap = new HashMap<>();
			if (StpUtil.isLogin()) {
				Set<Long> pictureIds = simpleVOS.stream().map(PictureHomeVO::getId).collect(Collectors.toSet());
				List<PictureInteraction> pictureInteractions = pictureInteractionDomainService.list(new LambdaQueryWrapper<PictureInteraction>()
						.eq(PictureInteraction::getUserId, StpUtil.getLoginIdAsLong())
						.in(PictureInteraction::getPictureId, pictureIds)
				);
				if (CollUtil.isNotEmpty(pictureInteractions)) {
					pictureInteractions.forEach(pi->{
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
			simpleVOS.forEach(picture -> {
				// 设置作者信息
				Long userId = picture.getUserId();
				if (userListMap.containsKey(userId)) {
					picture.setUserName(userListMap.get(userId).get(0).getUserName());
					picture.setUserAvatar(userListMap.get(userId).get(0).getUserAvatar());
				}
				// 设置当前登录用户点赞和收藏信息
				picture.setLoginUserIsLike(likeMap.getOrDefault(picture.getId(), false));
				picture.setLoginUserIsCollect(collectMap.getOrDefault(picture.getId(), false));
				// TODO 分类和标签的处理
			});
		}
		// TODO 是否需要加入到缓存?
		return new PageVO<>(picturePage.getCurrent()
				, picturePage.getSize()
				, picturePage.getTotal()
				, picturePage.getPages()
				, simpleVOS
		);
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 图片管理分页列表
	 */
	@Override
	public PageVO<PictureVO> getPicturePageListAsManage(PictureQueryRequest pictureQueryRequest) {
		Page<Picture> picturePage = pictureDomainService.getPicturePageListAsManage(
				pictureQueryRequest.getPage(Picture.class), this.getLambdaQueryWrapper(pictureQueryRequest)
		);
		List<PictureVO> pictureVOS = picturePage.getRecords().stream()
				.map(PictureAssembler::toPictureVO)
				.collect(Collectors.toList());
		if (!pictureVOS.isEmpty()) {
			// 查询图片的用户信息
			Set<Long> userIds = pictureVOS.stream().map(PictureVO::getUserId).collect(Collectors.toSet());
			Map<Long, List<User>> userListMap = userApplicationService.getUserListByUserIds(userIds)
					.stream().collect(Collectors.groupingBy(User::getId));
			// 查询分类信息
			Set<Long> categoryIds = pictureVOS.stream().map(PictureVO::getCategoryId).collect(Collectors.toSet());
			Map<Long, List<Category>> categoryListMap = categoryApplicationService.getCategoryListByIds(categoryIds)
					.stream().collect(Collectors.groupingBy(Category::getId));
			pictureVOS.forEach(picture -> {
				Long userId = picture.getUserId();
				if (userListMap.containsKey(userId)) {
					picture.setUserInfo(userListMap.get(userId).get(0));
				}
				Long categoryId = picture.getCategoryId();
				if (categoryListMap.containsKey(categoryId)) {
					picture.setCategoryInfo(categoryListMap.get(categoryId).get(0));
				}
			});
		}
		return new PageVO<>(picturePage.getCurrent()
				, picturePage.getSize()
				, picturePage.getTotal()
				, picturePage.getPages()
				, pictureVOS
		);
	}

	/**
	 * 获取个人空间图片分页列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 个人空间图片分页列表
	 */
	@Override
	public PageVO<PictureVO> getPicturePageListAsPersonSpace(PictureQueryRequest pictureQueryRequest) {
		User loginUser = userApplicationService.getLoginUserDetail();
		// 查询当前登录用户是否创建了空间
		Space space = spaceApplicationService.getSpaceInfoByUserId(loginUser.getId());
		ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "当前用户未创建个人空间");
		pictureQueryRequest.setSpaceId(space.getId());
		Page<Picture> picturePage = pictureDomainService.getPicturePageListAsPersonSpace(
				pictureQueryRequest.getPage(Picture.class), this.getLambdaQueryWrapper(pictureQueryRequest)
		);
		List<PictureVO> pictureVOS = picturePage.getRecords().stream()
				.map(PictureAssembler::toPictureVO)
				.collect(Collectors.toList());
		if (!pictureVOS.isEmpty()) {
			// 查询分类信息
			Set<Long> categoryIds = pictureVOS.stream().map(PictureVO::getCategoryId).collect(Collectors.toSet());
			Map<Long, List<Category>> categoryListMap = categoryApplicationService.getCategoryListByIds(categoryIds)
					.stream().collect(Collectors.groupingBy(Category::getId));
			pictureVOS.forEach(picture -> {
				Long categoryId = picture.getCategoryId();
				if (categoryListMap.containsKey(categoryId)) {
					picture.setCategoryInfo(categoryListMap.get(categoryId).get(0));
				}
			});
		}
		return new PageVO<>(picturePage.getCurrent()
				, picturePage.getSize()
				, picturePage.getTotal()
				, picturePage.getPages()
				, pictureVOS
		);
	}

	/**
	 * 爬取图片
	 *
	 * @param pictureGrabRequest 图片抓取请求
	 * @return 爬取的图片列表
	 */
	@Override
	public List<GrabPictureResult> grabPicture(PictureGrabRequest pictureGrabRequest) {
		String keyword = pictureGrabRequest.getKeyword();
		if (StrUtil.isEmpty(keyword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "关键词不能为空");
		}
		return pictureDomainService.grabPicture(pictureGrabRequest);
	}

	// /**
	//  * 根据颜色搜索图片
	//  *
	//  * @param pictureColorSearchRequest 图片颜色搜索请求
	//  * @return 图片列表
	//  */
	// @Override
	// public List<PictureVO> searchPictureByColor(PictureColorSearchRequest pictureColorSearchRequest) {
	//	
	// 	// 3. 查询该空间下所有图片（必须有主色调）
	// 	List<TePicture> tePictureList = pictureDomainService.lambdaQuery()
	// 			.eq(TePicture::getSpaceId, spaceId)
	// 			.isNotNull(TePicture::getPicColor)
	// 			.list();
	// 	// 如果没有图片，直接返回空列表
	// 	if (CollUtil.isEmpty(tePictureList)) {
	// 		return Collections.emptyList();
	// 	}
	// 	// 将目标颜色转为 Color 对象
	// 	Color targetColor = Color.decode(picColor);
	// 	// 4. 计算相似度并排序
	// 	List<TePicture> sortedTePictures = tePictureList.stream()
	// 			.sorted(Comparator.comparingDouble(picture -> {
	// 				// 提取图片主色调
	// 				String hexColor = picture.getPicColor();
	// 				// 没有主色调的图片放到最后
	// 				if (StrUtil.isBlank(hexColor)) {
	// 					return Double.MAX_VALUE;
	// 				}
	// 				Color pictureColor = Color.decode(hexColor);
	// 				// 越大越相似
	// 				return -ColorSimilarUtils.calculateSimilarity(targetColor, pictureColor);
	// 			}))
	// 			// 取前 12 个
	// 			.limit(12)
	// 			.collect(Collectors.toList());
	//
	// 	// 转换为 PictureVO
	// 	return sortedTePictures.stream()
	// 			.map(PictureVO::objToVo)
	// 			.collect(Collectors.toList());
	// }

	// endregion 查询相关

	/**
	 * 图片下载
	 *
	 * @param pictureId 图片 ID
	 * @return 原图地址
	 */
	@Override
	public String pictureDownload(Long pictureId) {
		StpUtil.checkLogin();
		Picture picture = pictureDomainService.getPictureById(pictureId);
		if (picture == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在");
		}
		// 更新互动类型数量
		pictureDomainService.updateInteractionNum(pictureId, PictureInteractionTypeEnum.DOWNLOAD.getKey(), 1);
		return picture.getOriginUrl();
	}

	/**
	 * 图片分享
	 *
	 * @param pictureId 图片 ID
	 * @return true
	 */
	@Override
	public Boolean pictureShare(Long pictureId) {
		StpUtil.checkLogin();
		Picture picture = pictureDomainService.getPictureById(pictureId);
		if (picture == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在!");
		}
		if (!PictureShareStatusEnum.isShare(picture.getIsShare())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "当前图片作者不允许分享!");
		}
		// 更新互动类型数量
		pictureDomainService.updateInteractionNum(pictureId, PictureInteractionTypeEnum.SHARE.getKey(), 1);
		return true;
	}

	/**
	 * 图片查看
	 *
	 * @param pictureId 图片 ID
	 */
	@Override
	public void pictureView(Long pictureId) {
		// 更新互动类型数量
		pictureDomainService.updateInteractionNum(pictureId, PictureInteractionTypeEnum.VIEW.getKey(), 1);
	}

	/**
	 * 图片点赞或收藏
	 *
	 * @param pictureInteractionRequest 图片互动请求
	 * @return true
	 */
	@Override
	public Boolean pictureLikeOrCollect(PictureInteractionRequest pictureInteractionRequest) {
		Long pictureId = pictureInteractionRequest.getId();
		Picture picture = pictureDomainService.getPictureById(pictureId);
		if (picture == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在!");
		}
		User loginUser = userApplicationService.getLoginUserDetail();
		Long userId = loginUser.getId();
		LambdaQueryWrapper<PictureInteraction> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(PictureInteraction::getPictureId, pictureId);
		queryWrapper.eq(PictureInteraction::getUserId, userId);
		if (PictureInteractionTypeEnum.LIKE.getKey().equals(pictureInteractionRequest.getType())) {
			queryWrapper.eq(PictureInteraction::getInteractionType, PictureInteractionTypeEnum.LIKE.getKey());
		} else if (PictureInteractionTypeEnum.COLLECT.getKey().equals(pictureInteractionRequest.getType())) {
			queryWrapper.eq(PictureInteraction::getInteractionType, PictureInteractionTypeEnum.COLLECT.getKey());
		} else {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片互动类型错误!");
		}
		PictureInteraction pictureInteraction = pictureInteractionDomainService.getOne(queryWrapper);
		if (pictureInteraction == null) {
			pictureInteraction = new PictureInteraction();
			pictureInteraction.setPictureId(pictureId);
			pictureInteraction.setUserId(userId);
			pictureInteraction.setInteractionType(pictureInteractionRequest.getType());
			pictureInteractionDomainService.save(pictureInteraction);
			// 更新互动类型数量
			pictureDomainService.updateInteractionNum(pictureId, pictureInteractionRequest.getType(), 1);
		} else {
			pictureInteraction.setInteractionStatus(pictureInteractionRequest.getChange());
			pictureInteractionDomainService.update(new LambdaUpdateWrapper<PictureInteraction>()
					.set(PictureInteraction::getInteractionStatus, pictureInteractionRequest.getChange())
					.eq(PictureInteraction::getUserId, userId)
					.eq(PictureInteraction::getPictureId, pictureId)
					.eq(PictureInteraction::getInteractionType, pictureInteractionRequest.getType())
			);
			if (PictureInteractionStatusEnum.CANCEL.getKey().equals(pictureInteraction.getInteractionStatus())) {
				// 更新互动类型数量
				pictureDomainService.updateInteractionNum(pictureId, pictureInteractionRequest.getType(), -1);
			} else {
				// 更新互动类型数量
				pictureDomainService.updateInteractionNum(pictureId, pictureInteractionRequest.getType(), 1);
			}
		}
		return true;
	}

	// /**
	//  * 创建图片扩图任务
	//  *
	//  * @param createPictureOutPaintingTaskRequest 图片扩图任务请求
	//  * @param loginTUser                          当前登录用户
	//  * @return 扩图任务响应
	//  */
	// @Override
	// public CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginTUser) {
	// 	return pictureDomainService.createPictureOutPaintingTask(createPictureOutPaintingTaskRequest, loginTUser);
	// }
	//
	// /**
	//  * 获取图片指定字段列表
	//  *
	//  * @param queryWrapper 查询条件
	//  * @return 图片指定字段的列表
	//  */
	// @Override
	// public List<Object> getPictureAppointFieldList(QueryWrapper<TePicture> queryWrapper) {
	// 	return pictureDomainService.getBaseMapper().selectObjs(queryWrapper);
	// }
	//
	// /**
	//  * 获取图片指定字段 Map 列表
	//  *
	//  * @param queryWrapper 查询条件
	//  * @return 图片指定字段的列表
	//  */
	// @Override
	// public List<Map<String, Object>> getPictureAppointFieldMaps(QueryWrapper<TePicture> queryWrapper) {
	// 	return pictureDomainService.getBaseMapper().selectMaps(queryWrapper);
	// }
}




