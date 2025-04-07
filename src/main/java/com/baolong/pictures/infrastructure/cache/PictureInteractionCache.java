package com.baolong.pictures.infrastructure.cache;

import com.baolong.pictures.domain.picture.aggregate.enums.PictureExpandStatusEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureReviewStatusEnum;
import com.baolong.pictures.infrastructure.common.constant.CacheKeyConstant;
import com.baolong.pictures.infrastructure.manager.redis.RedisCache;
import com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis.PictureDO;
import com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis.PicturePersistenceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图片交互缓存
 *
 * @author Silas Yan 2025-03-29:17:47
 */
@Slf4j
@Service
public class PictureInteractionCache {

	@Resource
	private PicturePersistenceService picturePersistenceService;
	@Resource
	private RedisCache redisCache;

	public void init() {
		log.info(">>> 初始化[图片互动]数据缓存...");

		Set<String> keys = redisCache.getKeys(CacheKeyConstant.PICTURE_INTERACTION_KEY_PREFIX);
		if (!keys.isEmpty()) {
			log.info("<<< [图片互动]数据缓存已存在!");
			return;
		}

		int size = 1000;
		long total = 0;

		while (true) {
			// 分批加载所有需要缓存的图片数据
			Page<PictureDO> page = picturePersistenceService.page(new Page<>(total, size), new LambdaQueryWrapper<PictureDO>()
					.eq(PictureDO::getReviewStatus, PictureReviewStatusEnum.PASS.getKey())
					.ne(PictureDO::getExpandStatus, PictureExpandStatusEnum.YES.getKey())
			);
			List<PictureDO> pictures = page.getRecords();
			// 批量写入Redis
			pictures.forEach(pic -> {
				String key = CacheKeyConstant.PICTURE_INTERACTION_KEY_PREFIX + pic.getId();
				Map<String, Object> interactions = Map.of(
						"0", pic.getLikeQuantity(),
						"1", pic.getCollectQuantity(),
						"2", pic.getDownloadQuantity(),
						"3", pic.getShareQuantity(),
						"4", pic.getViewQuantity(),
						"5", pic.getCreateTime().getTime()
				);
				redisCache.hSets(key, interactions);
			});

			total += pictures.size();
			log.info("--- 已初始化 {} 条图片互动数据", total);
			if (pictures.size() < size) {
				break;
			}
		}
		log.info("<<< [图片互动]数据缓存初始化完成，共加载 {} 条记录", total);
	}
}
