package com.baolong.pictures.domain.picture.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.domain.picture.entity.Picture;
import com.baolong.pictures.domain.picture.enums.PictureInteractionTypeEnum;
import com.baolong.pictures.domain.picture.enums.PictureReviewStatusEnum;
import com.baolong.pictures.domain.picture.service.PictureDomainService;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.api.aliyunai.AliYunAiApi;
import com.baolong.pictures.infrastructure.api.cos.CosManager;
import com.baolong.pictures.infrastructure.api.grab.GrabPictureManager;
import com.baolong.pictures.infrastructure.api.grab.enums.GrabSourceEnum;
import com.baolong.pictures.infrastructure.api.grab.model.GrabPictureResult;
import com.baolong.pictures.infrastructure.cache.LocalCacheConfig;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.constant.CacheKeyConstant;
import com.baolong.pictures.infrastructure.constant.TextConstant;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.manager.redis.RedisCache;
import com.baolong.pictures.infrastructure.manager.upload.UploadPicture;
import com.baolong.pictures.infrastructure.manager.upload.picture.UploadPictureFile;
import com.baolong.pictures.infrastructure.manager.upload.picture.UploadPictureUrl;
import com.baolong.pictures.infrastructure.manager.upload.picture.model.UploadPictureResult;
import com.baolong.pictures.infrastructure.repository.PictureRepository;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baolong.pictures.interfaces.web.request.picture.PictureGrabRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureQueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 图片表 (picture) - 领域服务实现
 */
@Slf4j
@Service
public class PictureDomainServiceImpl extends ServiceImpl<PictureRepository, Picture> implements PictureDomainService {

	@Resource
	private UploadPictureFile uploadPictureFile;
	@Resource
	private UploadPictureUrl uploadPictureUrl;
	@Resource
	private GrabPictureManager grabPictureManager;

	@Resource
	private CosManager cosManager;
	@Resource
	private AliYunAiApi aliYunAiApi;
	@Resource
	private RedisCache redisCache;

	// region 其他方法

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@Override
	public LambdaQueryWrapper<Picture> getLambdaQueryWrapper(PictureQueryRequest pictureQueryRequest) {
		LambdaQueryWrapper<Picture> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		String searchText = pictureQueryRequest.getSearchText();
		Long id = pictureQueryRequest.getId();
		String originFormat = pictureQueryRequest.getOriginFormat();
		Integer originWidth = pictureQueryRequest.getOriginWidth();
		Integer originHeight = pictureQueryRequest.getOriginHeight();
		Double originScale = pictureQueryRequest.getOriginScale();
		String originColor = pictureQueryRequest.getOriginColor();
		String picName = pictureQueryRequest.getPicName();
		String picDesc = pictureQueryRequest.getPicDesc();
		Long category = pictureQueryRequest.getCategoryId();
		String tags = pictureQueryRequest.getTags();
		Long userId = pictureQueryRequest.getUserId();
		Long spaceId = pictureQueryRequest.getSpaceId();
		Integer reviewStatus = pictureQueryRequest.getReviewStatus();
		String reviewMessage = pictureQueryRequest.getReviewMessage();
		Long reviewerUser = pictureQueryRequest.getReviewerUser();
		Date startCreateTime = pictureQueryRequest.getStartCreateTime();
		Date endCreateTime = pictureQueryRequest.getEndCreateTime();
		Date startEditTime = pictureQueryRequest.getStartEditTime();
		Date endEditTime = pictureQueryRequest.getEndEditTime();
		lambdaQueryWrapper.and(StrUtil.isNotEmpty(searchText), lqw ->
				lqw.like(Picture::getPicName, searchText)
						.or().like(Picture::getPicDesc, searchText)
						.or().apply("FIND_IN_SET ('" + searchText + "', tags) > 0")
		);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(id), Picture::getId, id);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(originFormat), Picture::getOriginFormat, originFormat);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(originWidth), Picture::getOriginWidth, originWidth);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(originHeight), Picture::getOriginHeight, originHeight);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(originScale), Picture::getOriginScale, originScale);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(originColor), Picture::getOriginColor, originColor);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(picName), Picture::getPicName, picName);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(picDesc), Picture::getPicDesc, picDesc);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(category), Picture::getCategoryId, category);
		/*// TODO 标签没处理
		if (CollUtil.isNotEmpty(tags)) {
			StringBuilder FIND_IN_SET_SQL = new StringBuilder(" (");
			for (String tag : tags) {
				FIND_IN_SET_SQL.append(" FIND_IN_SET (").append(tag).append(", tags) > 0 OR");
			}
			// 去掉字符串 FIND_IN_SET_SQL 末尾的 OR
			if (FIND_IN_SET_SQL.length() > 0 && FIND_IN_SET_SQL.lastIndexOf("OR") != -1) {
				FIND_IN_SET_SQL.setLength(FIND_IN_SET_SQL.lastIndexOf("OR"));
			}
			FIND_IN_SET_SQL.append(") ");
			queryWrapper.apply(FIND_IN_SET_SQL.toString());
		}*/
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(userId), Picture::getUserId, userId);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(spaceId), Picture::getSpaceId, spaceId);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(reviewStatus), Picture::getReviewStatus, reviewStatus);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(reviewMessage), Picture::getReviewMessage, reviewMessage);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(reviewerUser), Picture::getReviewerUser, reviewerUser);
		lambdaQueryWrapper.ge(ObjUtil.isNotEmpty(startCreateTime), Picture::getCreateTime, startCreateTime);
		lambdaQueryWrapper.lt(ObjUtil.isNotEmpty(endCreateTime), Picture::getCreateTime, endCreateTime);
		lambdaQueryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), Picture::getEditTime, startEditTime);
		lambdaQueryWrapper.lt(ObjUtil.isNotEmpty(endEditTime), Picture::getEditTime, endEditTime);
		// 处理排序规则
		if (pictureQueryRequest.isMultipleSort()) {
			List<PageRequest.Sort> sorts = pictureQueryRequest.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(
							StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(Picture.class, sortField)
					);
				});
			}
		} else {
			PageRequest.Sort sort = pictureQueryRequest.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(
						StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(Picture.class, sortField)
				);
			} else {
				lambdaQueryWrapper.orderByDesc(Picture::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	/**
	 * 校验并填充审核参数
	 *
	 * @param picture   图片对象
	 * @param loginUser 登录用户
	 */
	@Override
	public void checkAndFillReviewParams(Picture picture, User loginUser) {
		if (loginUser.isAdmin()) {
			picture.setReviewStatus(PictureReviewStatusEnum.PASS.getKey());
			picture.setReviewMessage(TextConstant.REVIEW_AUTO);
			picture.setReviewerUser(loginUser.getId());
			picture.setReviewTime(new Date());
		} else {
			picture.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getKey());
		}
	}

	/**
	 * 校验图片操作权限
	 *
	 * @param picture   图片对象
	 * @param loginUser 登录用户
	 */
	@Override
	public void checkPictureChangeAuth(Picture picture, User loginUser) {
		if (!loginUser.isAdmin() && !picture.getUserId().equals(loginUser.getId())) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有操作权限");
		}
	}

	/**
	 * 填充图片名称规则
	 *
	 * @param pictureList 图片列表
	 * @param nameRule    名称规则
	 */
	@Override
	public void fillPictureNameRuleBatch(List<Picture> pictureList, String nameRule) {
		AtomicInteger count = new AtomicInteger(1);
		// 遍历更新图片的相关信息
		pictureList.forEach(picture -> {
			// 处理命名规则 规则: 名称_{序号}
			if (StrUtil.isNotBlank(nameRule)) {
				String pictureName = nameRule.replaceAll("\\{序号}", String.valueOf(count.getAndIncrement()));
				picture.setPicName(pictureName);
			}
		});
	}

	/**
	 * 清理图片文件
	 *
	 * @param picture 图片对象
	 */
	@Async
	@Override
	public void clearPictureFile(Picture picture) {
		if (picture == null) return;
		String originPath = picture.getOriginPath();
		String compressPath = picture.getCompressPath();
		String thumbnailPath = picture.getThumbnailPath();
		if (StrUtil.isNotEmpty(originPath)) {
			log.info("删除图片: {}", originPath);
			cosManager.deleteObject(originPath);
		}
		if (StrUtil.isNotEmpty(compressPath)) {
			log.info("删除图片: {}", compressPath);
			cosManager.deleteObject(compressPath);
		}
		if (StrUtil.isNotEmpty(thumbnailPath)) {
			log.info("删除图片: {}", thumbnailPath);
			cosManager.deleteObject(thumbnailPath);
		}
	}

	// endregion 其他方法

	// region 增删改相关（包含上传图片）

	/**
	 * 上传图片
	 *
	 * @param pictureInputSource 图片输入源
	 * @param picture            图片
	 * @return 是否成功
	 */
	@Override
	public Boolean uploadPicture(Object pictureInputSource, Picture picture) {
		UploadPicture uploadTemplate = pictureInputSource instanceof String ? this.uploadPictureUrl : this.uploadPictureFile;
		// 路径, 例如: images/public/2025_03_08/
		String pathPrefix = "images/%s/" + DateUtil.format(new Date(), "yyyy_MM_dd") + "/";
		Long spaceId = picture.getSpaceId();
		if (ObjectUtil.isNotEmpty(spaceId) && !spaceId.equals(0L)) {
			pathPrefix = String.format(pathPrefix, spaceId);
		} else {
			pathPrefix = String.format(pathPrefix, "public");
		}
		// 调用上传图片
		UploadPictureResult uploadPictureResult = uploadTemplate.uploadFile(pictureInputSource, pathPrefix, true);
		BeanUtils.copyProperties(uploadPictureResult, picture);
		return this.saveOrUpdate(picture);
	}

	/**
	 * 删除图片
	 *
	 * @param pictureId 图片 ID
	 * @return 是否成功
	 */
	@Override
	public Boolean deletePicture(Long pictureId) {
		return this.removeById(pictureId);
	}

	/**
	 * 更新图片
	 *
	 * @param picture 图片实体
	 * @return 是否成功
	 */
	@Override
	public Boolean updatePicture(Picture picture) {
		return this.updateById(picture);
	}

	/**
	 * 编辑图片
	 *
	 * @param picture 图片实体
	 * @return 是否成功
	 */
	@Override
	public Boolean editPicture(Picture picture) {
		return this.updateById(picture);
	}

	/**
	 * 审核图片
	 *
	 * @param pictureList 图片列表
	 * @return 是否成功
	 */
	@Override
	public Boolean reviewPicture(List<Picture> pictureList) {
		return this.updateBatchById(pictureList);
	}

	/**
	 * 编辑图片（批量）
	 *
	 * @param pictureList 图片列表
	 * @return 是否成功
	 */
	@Override
	public Boolean editPictureBatch(List<Picture> pictureList) {
		return this.updateBatchById(pictureList);
	}

	// endregion 增删改相关（包含上传图片）

	// region 查询相关

	/**
	 * 根据图片 ID 判断图片是否存在
	 *
	 * @param pictureId 图片 ID
	 * @return 是否存在
	 */
	@Override
	public Boolean existPictureById(Long pictureId) {
		return this.getBaseMapper().exists(new LambdaQueryWrapper<Picture>().eq(Picture::getId, pictureId));
	}

	/**
	 * 根据图片 ID 获取图片信息
	 *
	 * @param pictureId 图片 ID
	 * @return 图片信息
	 */
	@Override
	public Picture getPictureById(Long pictureId) {
		return this.getById(pictureId);
	}

	/**
	 * 获取首页图片列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 首页图片列表
	 */
	@Override
	public Page<Picture> getPicturePageListAsHome(Page<Picture> page, LambdaQueryWrapper<Picture> lambdaQueryWrapper) {
		lambdaQueryWrapper.select(
				Picture::getId, Picture::getPicUrl, Picture::getThumbnailUrl, Picture::getCategoryId, Picture::getUserId,
				Picture::getPicName, Picture::getPicDesc, Picture::getViewQuantity, Picture::getLikeQuantity,
				Picture::getCollectQuantity, Picture::getDownloadQuantity, Picture::getShareQuantity, Picture::getIsShare,
				Picture::getReviewStatus
		);
		lambdaQueryWrapper.eq(Picture::getReviewStatus, PictureReviewStatusEnum.PASS.getKey());

		// region 使用并构建缓存

		// 1.构建缓存 KEY
		String KEY = String.format(CacheKeyConstant.PICTURE_LIST_KEY
				, DigestUtils.md5DigestAsHex(
						(JSONUtil.toJsonStr(page) + JSONUtil.toJsonStr(lambdaQueryWrapper)).getBytes())
		);
		// 2.从本地缓存中查询, 如果本地缓存命中，返回结果
		String localCache = LocalCacheConfig.PICTURE_LOCAL_CACHE.getIfPresent(KEY);
		if (StrUtil.isNotEmpty(localCache)) {
			log.info("获取图片列表[Local 缓存]");
			return JSONUtil.toBean(localCache, new TypeReference<Page<Picture>>() {}, true);
		}
		// 3.查询 Redis, 如果 Redis 命中，返回结果
		String redisCache = this.redisCache.get(KEY);
		if (StrUtil.isNotEmpty(redisCache)) {
			log.info("获取图片列表[Redis 缓存]");
			// 设置到本地缓存
			LocalCacheConfig.PICTURE_LOCAL_CACHE.put(KEY, redisCache);
			return JSONUtil.toBean(redisCache, new TypeReference<Page<Picture>>() {}, true);
		}
		// 4.查询数据库, 存入 Redis 和 本地缓存
		Page<Picture> picturePage = this.page(page, lambdaQueryWrapper);
		log.info("获取图片列表[MySQL 查询]");
		// 存入 本地缓存, 已经配置了 5 分钟过期
		LocalCacheConfig.PICTURE_LOCAL_CACHE.put(KEY, JSONUtil.toJsonStr(picturePage));
		// 存入 Redis, 5 分钟过期
		this.redisCache.set(KEY, JSONUtil.toJsonStr(picturePage), 5, TimeUnit.MINUTES);

		// endregion 使用并构建缓存

		return picturePage;
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 图片管理分页列表
	 */
	@Override
	public Page<Picture> getPicturePageListAsManage(Page<Picture> page, LambdaQueryWrapper<Picture> lambdaQueryWrapper) {
		return this.page(page, lambdaQueryWrapper);
	}

	/**
	 * 获取个人空间图片分页列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 个人空间图片分页列表
	 */
	@Override
	public Page<Picture> getPicturePageListAsPersonSpace(Page<Picture> page, LambdaQueryWrapper<Picture> lambdaQueryWrapper) {
		return this.page(page, lambdaQueryWrapper);
	}

	/**
	 * 爬取图片
	 *
	 * @param pictureGrabRequest 图片抓取请求
	 * @return 爬取的图片列表
	 */
	@Override
	public List<GrabPictureResult> grabPicture(PictureGrabRequest pictureGrabRequest) {
		String grabSource = pictureGrabRequest.getGrabSource();
		GrabSourceEnum grabSourceEnum = GrabSourceEnum.getEnumByKey(grabSource);
		if (grabSourceEnum == null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持的爬取源");
		}
		Integer randomSeed = pictureGrabRequest.getRandomSeed();
		if (randomSeed == null) {
			randomSeed = RandomUtil.randomInt(1, 20);
		}
		List<GrabPictureResult> grabPictureResults = new ArrayList<>();
		int whileCount = 1;
		Integer grabCount = pictureGrabRequest.getGrabCount();
		while (grabPictureResults.size() < grabCount) {
			grabPictureResults = grabPictureManager.grabPictureByBing(
					grabSource, pictureGrabRequest.getKeyword(), randomSeed, grabCount
			);
			log.info("第 {} 次爬取, 数据: {}", whileCount++, JSONUtil.parseObj(grabPictureResults));
		}
		if (grabPictureResults.size() > grabCount) {
			grabPictureResults = grabPictureResults.subList(0, grabCount);
		}
		// 图片名称处理
		String namePrefix = pictureGrabRequest.getNamePrefix();
		if (StrUtil.isEmpty(namePrefix)) {
			namePrefix = "图片_{序号}";
		}
		AtomicInteger count = new AtomicInteger(1);
		String finalNamePrefix = namePrefix;
		grabPictureResults.forEach(grabPictureResult -> {
			String pictureName = finalNamePrefix.replaceAll("\\{序号}", String.valueOf(count.getAndIncrement()));
			grabPictureResult.setImageName(pictureName);
		});
		return grabPictureResults;
	}

	// endregion 查询相关

	/**
	 * 更新互动数量
	 *
	 * @param pictureId       图片 ID
	 * @param interactionType 互动类型
	 * @param num             变更数量
	 */
	@Override
	public void updateInteractionNum(Long pictureId, Integer interactionType, int num) {
		LambdaUpdateWrapper<Picture> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Picture::getId, pictureId);
		if (PictureInteractionTypeEnum.LIKE.getKey().equals(interactionType)) {
			updateWrapper.setSql("like_quantity = like_quantity + " + num);
		} else if (PictureInteractionTypeEnum.COLLECT.getKey().equals(interactionType)) {
			updateWrapper.setSql("collect_quantity = collect_quantity + " + num);
		} else if (PictureInteractionTypeEnum.DOWNLOAD.getKey().equals(interactionType)) {
			updateWrapper.setSql("download_quantity = download_quantity + " + num);
		} else if (PictureInteractionTypeEnum.SHARE.getKey().equals(interactionType)) {
			updateWrapper.setSql("share_quantity = share_quantity + " + num);
		} else if (PictureInteractionTypeEnum.VIEW.getKey().equals(interactionType)) {
			updateWrapper.setSql("view_quantity = view_quantity + " + num);
		} else {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持的互动类型");
		}
		this.update(updateWrapper);
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
	// 	// 获取图片信息
	// 	Long pictureId = createPictureOutPaintingTaskRequest.getPictureId();
	// 	TePicture tePicture = Optional.ofNullable(this.getById(pictureId))
	// 			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR));
	// 	// 权限校验
	// 	checkPictureAuth(loginTUser, tePicture);
	// 	// 构造请求参数
	// 	CreateOutPaintingTaskRequest taskRequest = new CreateOutPaintingTaskRequest();
	// 	CreateOutPaintingTaskRequest.Input input = new CreateOutPaintingTaskRequest.Input();
	// 	input.setImageUrl(tePicture.getUrl());
	// 	taskRequest.setInput(input);
	// 	BeanUtil.copyProperties(createPictureOutPaintingTaskRequest, taskRequest);
	// 	// 创建任务
	// 	return aliYunAiApi.createOutPaintingTask(taskRequest);
	// }

}




