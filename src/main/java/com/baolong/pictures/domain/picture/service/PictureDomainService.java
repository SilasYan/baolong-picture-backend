package com.baolong.pictures.domain.picture.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.domain.picture.aggregate.Picture;
import com.baolong.pictures.domain.picture.aggregate.PictureInteraction;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureExpandTypeEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureInteractionTypeEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureReviewStatusEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureUploadTypeEnum;
import com.baolong.pictures.domain.picture.repository.PictureInteractionRepository;
import com.baolong.pictures.domain.picture.repository.PictureRepository;
import com.baolong.pictures.infrastructure.api.bailian.BaiLianApi;
import com.baolong.pictures.infrastructure.api.bailian.model.BaiLianTaskResponse;
import com.baolong.pictures.infrastructure.api.bailian.model.CreateBaiLianTaskResponse;
import com.baolong.pictures.infrastructure.api.bailian.model.ExpandImageTaskRequest;
import com.baolong.pictures.infrastructure.api.cos.CosManager;
import com.baolong.pictures.infrastructure.api.grab.GrabPictureManager;
import com.baolong.pictures.infrastructure.api.grab.enums.GrabSourceEnum;
import com.baolong.pictures.infrastructure.api.grab.model.GrabPictureResult;
import com.baolong.pictures.infrastructure.api.pictureSearch.AbstractSearchPicture;
import com.baolong.pictures.infrastructure.api.pictureSearch.model.SearchPictureResult;
import com.baolong.pictures.infrastructure.common.constant.CacheKeyConstant;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.config.LocalCacheConfig;
import com.baolong.pictures.infrastructure.manager.redis.RedisCache;
import com.baolong.pictures.infrastructure.manager.upload.UploadPicture;
import com.baolong.pictures.infrastructure.manager.upload.picture.UploadPictureFile;
import com.baolong.pictures.infrastructure.manager.upload.picture.UploadPictureUrl;
import com.baolong.pictures.infrastructure.manager.upload.picture.model.UploadPictureResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 图片表 (picture) - 领域服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PictureDomainService {
	// 领域服务
	private final PictureRepository pictureRepository;
	private final PictureInteractionRepository pictureInteractionRepository;
	// 基础设施
	private final UploadPictureFile uploadPictureFile;
	private final UploadPictureUrl uploadPictureUrl;
	private final GrabPictureManager grabPictureManager;
	private final CosManager cosManager;
	private final Map<String, AbstractSearchPicture> searchServices;
	private final BaiLianApi baiLianApi;
	private final RedisCache redisCache;

	/**
	 * 上传图片
	 *
	 * @param pictureInputSource 图片输入源
	 * @param picture            图片领域对象
	 * @return 是否成功
	 */
	public Picture uploadPicture(Object pictureInputSource, Picture picture) {
		UploadPicture uploadTemplate = pictureInputSource instanceof String ? this.uploadPictureUrl : this.uploadPictureFile;
		// 路径, 例如: images/public/2025_03_08/
		String pathPrefix = "%s/%s/" + DateUtil.format(new Date(), "yyyy_MM_dd") + "/";
		Long userId = picture.getUserId();
		Long spaceId = picture.getSpaceId();
		if (ObjectUtil.isNotEmpty(spaceId) && !spaceId.equals(0L)) {
			pathPrefix = String.format(pathPrefix, PictureUploadTypeEnum.SPACE.getKey(), spaceId);
		} else {
			pathPrefix = String.format(pathPrefix, PictureUploadTypeEnum.PUBLIC.getKey(), userId);
		}
		// 调用上传图片
		UploadPictureResult uploadPictureResult = uploadTemplate.uploadFile(pictureInputSource, pathPrefix, true, null);
		BeanUtils.copyProperties(uploadPictureResult, picture);
		Long newPictureId;
		if (ObjUtil.isNotEmpty(picture.getPictureId())) {
			newPictureId = pictureRepository.updatePicture(picture);
		} else {
			newPictureId = pictureRepository.addPicture(picture);
		}
		// 初始化图片互动数据
		this.initPictureInteraction(newPictureId);
		// 查询图片
		return pictureRepository.getPictureByPictureId(newPictureId);
	}

	/**
	 * 删除图片
	 *
	 * @param pictureId 图片ID
	 */
	public void deletePicture(Long pictureId) {
		boolean existed = pictureRepository.deletePicture(pictureId);
		if (existed) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片删除失败");
	}

	/**
	 * 编辑图片
	 *
	 * @param picture 图片领域对象
	 */
	public void editPicture(Picture picture) {
		// 转化标签内容
		List<String> tagList = picture.getTagList();
		if (CollUtil.isNotEmpty(tagList)) {
			picture.setTags(String.join(",", tagList));
		}
		Long pictureId = pictureRepository.updatePicture(picture);
		// 初始化图片互动数据
		this.initPictureInteraction(pictureId);
	}

	/**
	 * 初始化图片互动数据
	 *
	 * @param pictureId 图片ID
	 */
	@Async
	public void initPictureInteraction(Long pictureId) {
		String key = CacheKeyConstant.PICTURE_INTERACTION_KEY_PREFIX + pictureId;
		Map<String, Object> interactions = redisCache.hGet(key);
		if (interactions == null || interactions.isEmpty()) {
			redisCache.hSets(key, Map.of(
					"0", 0,
					"1", 0,
					"2", 0,
					"3", 0,
					"4", 0,
					"5", new Date().getTime()
			));
		}
	}

	/**
	 * 清理图片文件
	 *
	 * @param picture 图片领域对象
	 */
	@Async
	public void clearPictureFile(Picture picture) {
		if (picture == null) return;
		String originPath = picture.getOriginPath();
		String compressPath = picture.getCompressPath();
		String thumbnailPath = picture.getThumbnailPath();
		if (StrUtil.isNotEmpty(originPath)) {
			log.info("清理图片文件: {}", originPath);
			cosManager.deleteObject(originPath);
		}
		if (StrUtil.isNotEmpty(compressPath)) {
			log.info("清理图片文件: {}", compressPath);
			cosManager.deleteObject(compressPath);
		}
		if (StrUtil.isNotEmpty(thumbnailPath)) {
			log.info("清理图片文件: {}", thumbnailPath);
			cosManager.deleteObject(thumbnailPath);
		}
	}

	/**
	 * 更新互动数量到Redis
	 *
	 * @param pictureId       图片 ID
	 * @param interactionType 互动类型
	 * @param num             变更数量
	 */
	public void updateInteractionNumByRedis(Long pictureId, Integer interactionType, int num) {
		String KEY = CacheKeyConstant.PICTURE_INTERACTION_KEY_PREFIX + pictureId;
		// 存储并递增
		redisCache.hIncrBy(KEY, String.valueOf(interactionType), num);
		// this.updateInteractionNum(pictureId, interactionType, num);
	}

	/**
	 * 更新互动数量
	 *
	 * @param pictureId       图片 ID
	 * @param interactionType 互动类型
	 * @param num             变更数量
	 */
	@Async
	public void updateInteractionNum(Long pictureId, Integer interactionType, int num) {
		boolean result = pictureRepository.updateInteractionNum(pictureId, interactionType, num);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "互动数量更新失败");
	}

	/**
	 * 审核图片
	 *
	 * @param pictureList 图片领域对象列表
	 * @param userId      用户ID
	 */
	public void reviewPicture(List<Picture> pictureList, Long userId) {
		pictureList.forEach(picture -> picture.setReviewerUser(userId));
		boolean result = pictureRepository.updatePictureByBatch(pictureList);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "审核图片失败");
	}

	/**
	 * 根据图片ID判断图片是否存在
	 *
	 * @param pictureId 图片ID
	 */
	public void existedPictureByPictureId(Long pictureId) {
		boolean existed = pictureRepository.existedPictureByPictureId(pictureId);
		if (existed) return;
		throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "图片不存在");
	}

	/**
	 * 判断用户是否可以操作图片
	 *
	 * @param pictureId 图片ID
	 * @param userId    用户ID
	 * @param isAdmin   是否是管理员
	 */
	public void canOperateInPicture(Long pictureId, Long userId, boolean isAdmin) {
		if (isAdmin) return;
		boolean existed = pictureRepository.existedPictureByPictureIdAndUserId(pictureId, userId);
		if (existed) return;
		throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有图片操作权限");
	}

	/**
	 * 操作图片点赞或收藏
	 *
	 * @param pictureInteraction 图片交互领域对象
	 */
	public void changePictureLikeOrCollect(PictureInteraction pictureInteraction) {
		pictureInteraction.setUserId(StpUtil.getLoginIdAsLong());
		boolean result = pictureInteractionRepository.changePictureLikeOrCollect(pictureInteraction);
		if (!result) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片互动操作失败!");
		}
	}

	/**
	 * 根据图片ID获取图片
	 *
	 * @param pictureId 图片ID
	 * @return 图片领域对象
	 */
	public Picture getPictureByPictureId(Long pictureId) {
		Picture picture = pictureRepository.getPictureByPictureId(pictureId);
		this.fillPictureInteraction(picture);
		// 更新图片操作类型数量
		this.updateInteractionNumByRedis(pictureId, PictureInteractionTypeEnum.VIEW.getKey(), 1);
		return picture;
	}

	/**
	 * 填充图片互动数据
	 *
	 * @param picture 图片领域对象
	 */
	private void fillPictureInteraction(Picture picture) {
		String key = CacheKeyConstant.PICTURE_INTERACTION_KEY_PREFIX + picture.getPictureId();
		Map<String, Object> interactions = redisCache.hGet(key);
		if (interactions != null) {
			if (ObjectUtil.isNotEmpty(interactions.get("0"))) {
				picture.setLikeQuantity(Integer.parseInt(interactions.get("0").toString()));
			}
			if (ObjectUtil.isNotEmpty(interactions.get("1"))) {
				picture.setCollectQuantity(Integer.parseInt(interactions.get("1").toString()));
			}
			if (ObjectUtil.isNotEmpty(interactions.get("2"))) {
				picture.setDownloadQuantity(Integer.parseInt(interactions.get("2").toString()));
			}
			if (ObjectUtil.isNotEmpty(interactions.get("3"))) {
				picture.setShareQuantity(Integer.parseInt(interactions.get("3").toString()));
			}
			if (ObjectUtil.isNotEmpty(interactions.get("4"))) {
				picture.setViewQuantity(Integer.parseInt(interactions.get("4").toString()));
			}
		}
	}

	/**
	 * 根据图片ID集合获取图片列表
	 *
	 * @param pictureIds 图片ID集合
	 * @return 图片列表
	 */
	public List<Picture> getPictureByPictureIds(Set<Long> pictureIds) {
		return pictureRepository.getPictureByPictureIds(pictureIds);
	}

	/**
	 * 根据图片ID和用户ID获取图片交互数据
	 *
	 * @param pictureId 图片ID
	 * @param userId    用户ID
	 * @return 图片交互数据
	 */
	public List<PictureInteraction> getPictureInteractionByPictureIdAndUserId(Long pictureId, Long userId) {
		return Optional
				.ofNullable(pictureInteractionRepository.getPictureInteractionByPictureIdAndUserId(pictureId, userId))
				.orElse(List.of());
	}

	/**
	 * 根据图片ID列表和用户ID获取图片交互数据
	 *
	 * @param pictureIds 图片ID列表
	 * @param userId     用户ID
	 * @return 图片交互数据
	 */
	public List<PictureInteraction> getPictureInteractionByPictureIdsAndUserId(Set<Long> pictureIds, Long userId) {
		return Optional
				.ofNullable(pictureInteractionRepository.getPictureInteractionByPictureIdsAndUserId(pictureIds, userId))
				.orElse(List.of());
	}

	/**
	 * 获取首页图片列表
	 *
	 * @param picture 图片领域对象
	 * @return 图片领域对象列表
	 */
	public PageVO<Picture> getPicturePageListAsHome(Picture picture) {
		PageVO<Picture> picturePageList = null;

		// 构建缓存 KEY 内容
		String cacheKeyContent = picture.getCurrent() + "_" + picture.getPageSize();
		Long categoryId = picture.getCategoryId();
		if (ObjectUtil.isNotEmpty(categoryId)) {
			cacheKeyContent = cacheKeyContent + "_" + categoryId;
		}

		// 1.构建缓存 KEY
		String KEY = String.format(CacheKeyConstant.HOME_PICTURE_LIST_KEY
				, DigestUtils.md5DigestAsHex(cacheKeyContent.getBytes())
		);
		// 2.从本地缓存中查询, 如果本地缓存命中，返回结果
		String localData = LocalCacheConfig.HOME_PICTURE_LOCAL_CACHE.getIfPresent(KEY);
		if (StrUtil.isNotEmpty(localData)) {
			log.info("首页图片列表[Local 缓存]");
			picturePageList = JSONUtil.toBean(localData, new TypeReference<PageVO<Picture>>() {
			}, true);
		}
		// 3.查询 Redis, 如果 Redis 命中，返回结果
		if (picturePageList == null) {
			String redisData = this.redisCache.get(KEY);
			if (StrUtil.isNotEmpty(redisData)) {
				log.info("首页图片列表[Redis 缓存]");
				// 设置到本地缓存
				LocalCacheConfig.HOME_PICTURE_LOCAL_CACHE.put(KEY, redisData);
				picturePageList = JSONUtil.toBean(redisData, new TypeReference<PageVO<Picture>>() {
				}, true);
			}
		}

		// 4.查询数据库, 存入 Redis 和 本地缓存
		if (picturePageList == null) {
			picture.setSpaceId(0L).setReviewStatus(PictureReviewStatusEnum.PASS.getKey())
					.setIsHome(true)
					.setExpandQuery(true);
			picturePageList = pictureRepository.getPicturePageList(picture);

			log.info("首页图片列表[MySQL 查询]");
			// 存入 本地缓存, 已经配置了 5 分钟过期
			LocalCacheConfig.HOME_PICTURE_LOCAL_CACHE.put(KEY, JSONUtil.toJsonStr(picturePageList));
			// 存入 Redis, 5 分钟过期
			this.redisCache.set(KEY, JSONUtil.toJsonStr(picturePageList), 5, TimeUnit.MINUTES);
		}

		// 动态设置图片的互动数据
		picturePageList.getRecords().forEach(this::fillPictureInteraction);

		return picturePageList;
	}

	/**
	 * 获取个人空间图片分页列表
	 *
	 * @param picture 图片领域对象
	 * @return 图片领域对象列表
	 */
	public PageVO<Picture> getPicturePageListAsPersonSpace(Picture picture) {
		return pictureRepository.getPicturePageList(picture);
	}

	/**
	 * 获取个人发布的图片分页列表
	 *
	 * @param picture 图片领域对象
	 * @return 个人发布的图片分页列表
	 */
	public PageVO<Picture> getPicturePageListAsPersonRelease(Picture picture) {
		picture.setSpaceId(0L).setUserId(StpUtil.getLoginIdAsLong());
		return pictureRepository.getPicturePageList(picture);
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param picture 图片领域对象
	 * @return 图片管理分页列表
	 */
	public PageVO<Picture> getPicturePageListAsManage(Picture picture) {
		return pictureRepository.getPicturePageList(picture);
	}

	/**
	 * 爬取图片
	 *
	 * @param picture 图片领域对象
	 * @return 爬取的图片列表
	 */
	public List<GrabPictureResult> grabPicture(Picture picture) {
		String grabSource = picture.getGrabSource();
		GrabSourceEnum grabSourceEnum = GrabSourceEnum.getEnumByKey(grabSource);
		if (grabSourceEnum == null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持的爬取源");
		}
		Integer randomSeed = picture.getRandomSeed();
		if (randomSeed == null) {
			randomSeed = RandomUtil.randomInt(1, 20);
		}
		List<GrabPictureResult> grabPictureResults = new ArrayList<>();
		int whileCount = 1;
		Integer grabCount = picture.getGrabCount();
		while (grabPictureResults.size() < grabCount) {
			grabPictureResults = grabPictureManager.grabPictureByBing(
					grabSource, picture.getKeyword(), randomSeed, grabCount
			);
			log.info("第 {} 次爬取, 数量: {}", whileCount++, grabPictureResults.size());
			randomSeed++;
		}
		if (grabPictureResults.size() > grabCount) {
			grabPictureResults = grabPictureResults.subList(0, grabCount);
		}
		// 图片名称处理
		String namePrefix = picture.getNamePrefix();
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

	/**
	 * 上传爬取图片
	 *
	 * @param picture 图片领域对象
	 */
	public void uploadPictureByGrab(Picture picture) {
		Long userId = StpUtil.getLoginIdAsLong();
		picture.setUserId(userId);
		// 路径, 例如: images/public/2025_03_08/
		String pathPrefix = "%s/%s/" + DateUtil.format(new Date(), "yyyy_MM_dd") + "/";
		pathPrefix = String.format(pathPrefix, PictureUploadTypeEnum.PUBLIC.getKey(), userId);
		// 调用上传图片
		UploadPictureResult uploadPictureResult = this.uploadPictureUrl
				.uploadFile(picture.getPictureUrl(), pathPrefix, true, picture.getPicName());
		BeanUtils.copyProperties(uploadPictureResult, picture);
		Long newPictureId = pictureRepository.addPicture(picture);
	}

	/**
	 * 以图搜图
	 *
	 * @param picture 图片领域对象
	 * @return 搜图的图片列表
	 */
	public List<SearchPictureResult> searchPicture(Picture picture) {
		Picture oldPicture = this.getPictureByPictureId(picture.getPictureId());
		if (oldPicture == null) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "图片不存在");
		}
		String searchSource = picture.getSearchSource();
		AbstractSearchPicture soSearchPicture = searchServices.get(searchSource + "SearchPicture");
		return soSearchPicture.execute(searchSource, oldPicture.getOriginUrl(), picture.getRandomSeed(), picture.getSearchCount());
	}

	/**
	 * 扩图
	 *
	 * @param picture 图片领域对象
	 * @return 扩图任务结果
	 */
	public CreateBaiLianTaskResponse expandPicture(Picture picture) {
		ExpandImageTaskRequest expandImageTaskRequest = new ExpandImageTaskRequest();
		expandImageTaskRequest.setInput(new ExpandImageTaskRequest.Input(picture.getPicUrl()));
		ExpandImageTaskRequest.Parameters parameters = new ExpandImageTaskRequest.Parameters()
				.setXScale(2.0f).setYScale(2.0f);
		if (PictureExpandTypeEnum.ANGLE.getKey().equals(picture.getExpandType())) {
			parameters.setAngle(45);
		}
		expandImageTaskRequest.setParameters(parameters);
		return baiLianApi.createExpandImageTask(expandImageTaskRequest);
	}

	/**
	 * 扩图查询
	 *
	 * @param taskId 任务ID
	 * @return 扩图任务结果
	 */
	public BaiLianTaskResponse expandPictureQuery(String taskId) {
		return baiLianApi.queryBaiLianTask(taskId);
	}
}
