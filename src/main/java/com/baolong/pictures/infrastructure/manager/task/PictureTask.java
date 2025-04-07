package com.baolong.pictures.infrastructure.manager.task;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baolong.pictures.infrastructure.common.constant.CacheKeyConstant;
import com.baolong.pictures.infrastructure.manager.redis.RedisCache;
import com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis.PictureDO;
import com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis.PicturePersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 图片定时任务
 *
 * @author Silas Yan 2025-03-29:17:21
 */
@Slf4j
@Component
public class PictureTask {

	@Resource
	private PicturePersistenceService picturePersistenceService;
	@Resource
	private RedisCache redisCache;

	/**
	 * 批量同步图片互动数据
	 */
	@Bean
	public Task batchSyncInteractions() {
		return () -> {
			log.info("开始同步互动数据到数据库");
			Set<String> keys = redisCache.getKeys(CacheKeyConstant.PICTURE_INTERACTION_KEY_PREFIX);
			if (keys == null || keys.isEmpty()) {
				log.info("没有需要同步的互动数据");
				return;
			}
			// 2. 将Set转为List以便分批
			List<String> keyList = new ArrayList<>(keys);
			int totalSize = keyList.size();
			int batchSize = 1000; // 每批数量
			int totalBatches = (totalSize + batchSize - 1) / batchSize; // 计算总批次数

			log.info("开始同步互动数据，共 {} 条，分 {} 批处理", totalSize, totalBatches);

			// 3. 分批处理
			for (int i = 0; i < totalBatches; i++) {
				int fromIndex = i * batchSize;
				int toIndex = Math.min((i + 1) * batchSize, totalSize);
				List<String> batchKeys = keyList.subList(fromIndex, toIndex);

				processBatch(batchKeys, i + 1, totalBatches);
			}
		};
	}

	/**
	 * 处理一批数据
	 *
	 * @param batchKeys    Redis键
	 * @param batchNumber  当前批号
	 * @param totalBatches 总批数
	 */
	private void processBatch(List<String> batchKeys, int batchNumber, int totalBatches) {
		log.info("正在处理第 {}/{} 批数据，数量 {}", batchNumber, totalBatches, batchKeys.size());

		try {
			// 准备批量更新
			List<PictureDO> updates = new ArrayList<>(batchKeys.size());

			batchKeys.forEach(key -> {
				try {
					Long pictureId = extractPictureId(key);
					Map<String, Object> interactions = redisCache.hGet(key);
					if (interactions == null || interactions.isEmpty()) {
						return;
					}
					PictureDO pictureDO = buildPictureUpdate(pictureId, interactions);
					updates.add(pictureDO);
				} catch (Exception e) {
					log.error("处理key {} 失败: {}", key, e.getMessage());
				}
			});

			if (!updates.isEmpty()) {
				// 执行更新推荐分数的方法
				this.calculateRecommendScore(updates);
				// 批量更新到数据库
				picturePersistenceService.updateBatchById(updates);
				log.info("第 {}/{} 批同步成功，更新 {} 条", batchNumber, totalBatches, updates.size());
			}
		} catch (Exception e) {
			log.error("第 {} 批处理失败", batchNumber, e);
		}
	}

	/**
	 * 提取图片ID
	 *
	 * @param key Redis键
	 * @return 图片ID
	 */
	private Long extractPictureId(String key) {
		String idStr = key.substring(key.lastIndexOf(":") + 1);
		return Long.parseLong(idStr);
	}

	/**
	 * 构建更新对象
	 *
	 * @param pictureId    图片ID
	 * @param interactions 互动数据
	 * @return 跟新对象
	 */
	private PictureDO buildPictureUpdate(Long pictureId, Map<String, Object> interactions) {
		PictureDO update = new PictureDO();
		update.setId(pictureId);

		interactions.forEach((field, value) -> {
			try {
				switch (Integer.parseInt(field)) {
					case 0:
						update.setLikeQuantity(Integer.parseInt(String.valueOf(ObjectUtil.isEmpty(value) ? '0' : value)));
						break;
					case 1:
						update.setCollectQuantity(Integer.parseInt(String.valueOf(ObjectUtil.isEmpty(value) ? '0' : value)));
						break;
					case 2:
						update.setDownloadQuantity(Integer.parseInt(String.valueOf(ObjectUtil.isEmpty(value) ? '0' : value)));
						break;
					case 3:
						update.setShareQuantity(Integer.parseInt(String.valueOf(ObjectUtil.isEmpty(value) ? '0' : value)));
						break;
					case 4:
						update.setViewQuantity(Integer.parseInt(String.valueOf(ObjectUtil.isEmpty(value) ? '0' : value)));
						break;
					case 5:
						update.setCreateTime(new Date(Long.parseLong(String.valueOf(value))));
						break;
					default:
						log.warn("未知的互动类型: {}", field);
				}
			} catch (NumberFormatException e) {
				log.error("互动数据格式错误 field={}, value={}", field, value);
			}
		});
		return update;
	}

	// region 下面是计算图片推荐分数

	@Value("${recommend.score.view}")
	private double view;
	@Value("${recommend.score.like}")
	private double like;
	@Value("${recommend.score.collect}")
	private double collect;
	@Value("${recommend.score.download}")
	private double download;
	@Value("${recommend.score.share}")
	private double share;
	@Value("${recommend.score.time:0.1}")
	private double time;

	/**
	 * 计算推荐评分
	 */
	public void calculateRecommendScore(List<PictureDO> pictureDOS) {
		log.info("↓↓↓↓↓↓↓↓↓↓ 开始[计算图片推荐评分] ↓↓↓↓↓↓↓↓↓↓");
		if (CollUtil.isEmpty(pictureDOS)) {
			log.info("↑↑↑↑↑↑↑↑↑↑ 结束[无计算内容] ↑↑↑↑↑↑↑↑↑↑");
			return;
		}
		pictureDOS.forEach(pic -> {
			pic.setRecommendScore(BigDecimal.valueOf(calculateScore(pic)));
		});
		log.info("↑↑↑↑↑↑↑↑↑↑ 结束[计算图片推荐评分] ↑↑↑↑↑↑↑↑↑↑");
	}

	/**
	 * 计算推荐评分
	 *
	 * @param pic 图片对象
	 * @return 评分
	 */
	private double calculateScore(PictureDO pic) {
		return calculateTimeScore(pic.getCreateTime()) +
				Math.log1p(pic.getViewQuantity()) * view +
				Math.log1p(pic.getLikeQuantity()) * like +
				Math.log1p(pic.getCollectQuantity()) * collect +
				Math.log1p(pic.getDownloadQuantity()) * download +
				Math.log1p(pic.getShareQuantity()) * share;
	}

	/**
	 * 计算时间衰减得分（指数衰减）
	 *
	 * @param publishTime 发布时间
	 * @return 评分
	 */
	private double calculateTimeScore(Date publishTime) {
		long hours = ChronoUnit.HOURS.between(publishTime.toInstant(), Instant.now());
		return Math.exp(-time * hours);
	}

	// endregion 下面是计算图片推荐分数
}
