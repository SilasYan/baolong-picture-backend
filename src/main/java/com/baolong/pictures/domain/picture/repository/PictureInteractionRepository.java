package com.baolong.pictures.domain.picture.repository;

import com.baolong.pictures.domain.picture.aggregate.PictureInteraction;

import java.util.List;
import java.util.Set;

/**
 * 图片交互表 (picture_interaction) - 仓储服务接口
 *
 * @author Baolong 2025年03月20 23:28
 * @version 1.0
 * @since 1.8
 */
public interface PictureInteractionRepository {

	/**
	 * 操作图片点赞或收藏
	 *
	 * @param pictureInteraction 图片交互领域对象
	 * @return 操作结果
	 */
	boolean changePictureLikeOrCollect(PictureInteraction pictureInteraction);

	/**
	 * 根据图片ID和用户ID获取图片交互数据
	 *
	 * @param pictureId 图片ID
	 * @param userId    用户ID
	 * @return 图片交互数据
	 */
	List<PictureInteraction> getPictureInteractionByPictureIdAndUserId(Long pictureId, Long userId);

	/**
	 * 根据图片ID列表和用户ID获取图片交互数据
	 *
	 * @param pictureIds 图片ID列表
	 * @param userId     用户ID
	 * @return 图片交互数据
	 */
	List<PictureInteraction> getPictureInteractionByPictureIdsAndUserId(Set<Long> pictureIds, Long userId);
}
