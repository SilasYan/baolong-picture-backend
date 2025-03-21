package com.baolong.pictures.domain.picture.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.domain.picture.aggregate.Picture;
import com.baolong.pictures.domain.picture.aggregate.PictureInteraction;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureReviewStatusEnum;
import com.baolong.pictures.domain.picture.repository.PictureInteractionRepository;
import com.baolong.pictures.domain.picture.repository.PictureRepository;
import com.baolong.pictures.infrastructure.api.cos.CosManager;
import com.baolong.pictures.infrastructure.api.grab.GrabPictureManager;
import com.baolong.pictures.infrastructure.api.grab.enums.GrabSourceEnum;
import com.baolong.pictures.infrastructure.api.grab.model.GrabPictureResult;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.manager.upload.UploadPicture;
import com.baolong.pictures.infrastructure.manager.upload.picture.UploadPictureFile;
import com.baolong.pictures.infrastructure.manager.upload.picture.UploadPictureUrl;
import com.baolong.pictures.infrastructure.manager.upload.picture.model.UploadPictureResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
		Long newPictureId;
		if (ObjUtil.isNotEmpty(picture.getPictureId())) {
			newPictureId = pictureRepository.updatePicture(picture);
		} else {
			newPictureId = pictureRepository.addPicture(picture);
		}
		// 查询图片
		return this.getPictureByPictureId(newPictureId);
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
		pictureRepository.updatePicture(picture);
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
		return pictureRepository.getPictureByPictureId(pictureId);
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
		picture.setSpaceId(0L).setReviewStatus(PictureReviewStatusEnum.PASS.getKey());
		// region 使用并构建缓存
		//
		// // 1.构建缓存 KEY
		// String KEY = String.format(CacheKeyConstant.PICTURE_LIST_KEY
		// 		, DigestUtils.md5DigestAsHex(
		// 				(JSONUtil.toJsonStr(page) + JSONUtil.toJsonStr(lambdaQueryWrapper)).getBytes())
		// );
		// // 2.从本地缓存中查询, 如果本地缓存命中，返回结果
		// String localCache = LocalCacheConfig.PICTURE_LOCAL_CACHE.getIfPresent(KEY);
		// if (StrUtil.isNotEmpty(localCache)) {
		// 	log.info("获取图片列表[Local 缓存]");
		// 	return JSONUtil.toBean(localCache, new TypeReference<Page<Picture>>() {
		// 	}, true);
		// }
		// // 3.查询 Redis, 如果 Redis 命中，返回结果
		// String redisCache = this.redisCache.get(KEY);
		// if (StrUtil.isNotEmpty(redisCache)) {
		// 	log.info("获取图片列表[Redis 缓存]");
		// 	// 设置到本地缓存
		// 	LocalCacheConfig.PICTURE_LOCAL_CACHE.put(KEY, redisCache);
		// 	return JSONUtil.toBean(redisCache, new TypeReference<Page<Picture>>() {
		// 	}, true);
		// }
		// // 4.查询数据库, 存入 Redis 和 本地缓存
		// Page<Picture> picturePage = this.page(page, lambdaQueryWrapper);
		// log.info("获取图片列表[MySQL 查询]");
		// // 存入 本地缓存, 已经配置了 5 分钟过期
		// LocalCacheConfig.PICTURE_LOCAL_CACHE.put(KEY, JSONUtil.toJsonStr(picturePage));
		// // 存入 Redis, 5 分钟过期
		// this.redisCache.set(KEY, JSONUtil.toJsonStr(picturePage), 5, TimeUnit.MINUTES);
		//
		// endregion 使用并构建缓存
		return pictureRepository.getPicturePageList(picture);
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
			log.info("第 {} 次爬取, 数据: {}", whileCount++, JSONUtil.parseObj(grabPictureResults));
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
}
