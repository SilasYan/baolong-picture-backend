package com.baolong.pictures.domain.picture.repository;

import com.baolong.pictures.domain.picture.aggregate.Picture;
import com.baolong.pictures.infrastructure.common.page.PageVO;

import java.util.List;

/**
 * 图片表 (picture) - 仓储服务接口
 *
 * @author Baolong 2025年03月20 23:23
 * @version 1.0
 * @since 1.8
 */
public interface PictureRepository {

	/**
	 * 新增图片
	 *
	 * @param picture 图片领域对象
	 * @return 图片ID
	 */
	Long addPicture(Picture picture);

	/**
	 * 更新图片
	 *
	 * @param picture 图片领域对象
	 * @return 图片ID
	 */
	Long updatePicture(Picture picture);

	/**
	 * 删除图片
	 *
	 * @param pictureId 图片 ID
	 * @return 是否成功
	 */
	boolean deletePicture(Long pictureId);

	/**
	 * 批量更新图片
	 *
	 * @param pictureList 图片领域对象列表
	 * @return 是否成功
	 */
	boolean updatePictureByBatch(List<Picture> pictureList);

	/**
	 * 更新互动数量
	 *
	 * @param pictureId       图片 ID
	 * @param interactionType 互动类型
	 * @param num             变更数量
	 * @return 是否成功
	 */
	boolean updateInteractionNum(Long pictureId, Integer interactionType, int num);

	/**
	 * 根据图片ID判断图片是否存在
	 *
	 * @param pictureId 图片ID
	 * @return 是否存在
	 */
	boolean existedPictureByPictureId(Long pictureId);

	/**
	 * 根据图片ID和用户ID判断图片是否存在
	 *
	 * @param pictureId 图片ID
	 * @param userId    用户ID
	 * @return 是否存在
	 */
	boolean existedPictureByPictureIdAndUserId(Long pictureId, Long userId);

	/**
	 * 根据图片ID获取图片
	 *
	 * @param pictureId 图片ID
	 * @return 图片领域对象
	 */
	Picture getPictureByPictureId(Long pictureId);

	/**
	 * 获取图片分页列表
	 *
	 * @param picture 图片领域对象
	 * @return 图片领域对象分页列表
	 */
	PageVO<Picture> getPicturePageList(Picture picture);
}
