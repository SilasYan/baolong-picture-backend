package com.baolong.pictures.infrastructure.persistence.picture.pictureInteraction.repository;

import com.baolong.pictures.domain.picture.aggregate.PictureInteraction;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureInteractionTypeEnum;
import com.baolong.pictures.domain.picture.repository.PictureInteractionRepository;
import com.baolong.pictures.infrastructure.persistence.picture.pictureInteraction.converter.PictureInteractionConverter;
import com.baolong.pictures.infrastructure.persistence.picture.pictureInteraction.mybatis.PictureInteractionDO;
import com.baolong.pictures.infrastructure.persistence.picture.pictureInteraction.mybatis.PictureInteractionPersistenceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 图片交互表 (picture_interaction) - 仓储服务实现
 *
 * @author Baolong 2025年03月20 23:29
 * @version 1.0
 * @since 1.8
 */
@Repository
@RequiredArgsConstructor
public class PictureInteractionRepositoryImpl implements PictureInteractionRepository {

	private final PictureInteractionPersistenceService pictureInteractionPersistenceService;

	/**
	 * 操作图片点赞或收藏
	 *
	 * @param pictureInteraction 图片交互领域对象
	 * @return 操作结果
	 */
	@Override
	public boolean changePictureLikeOrCollect(PictureInteraction pictureInteraction) {
		Long pictureId = pictureInteraction.getPictureId();
		Long userId = pictureInteraction.getUserId();
		Integer interactionType = pictureInteraction.getInteractionType();
		Integer interactionStatus = pictureInteraction.getInteractionStatus();
		LambdaQueryWrapper<PictureInteractionDO> lambdaQueryWrapper = new LambdaQueryWrapper<PictureInteractionDO>()
				.eq(PictureInteractionDO::getPictureId, pictureId)
				.eq(PictureInteractionDO::getUserId, userId);
		if (PictureInteractionTypeEnum.LIKE.getKey().equals(interactionType)) {
			lambdaQueryWrapper.eq(PictureInteractionDO::getInteractionType, PictureInteractionTypeEnum.LIKE.getKey());
		} else if (PictureInteractionTypeEnum.COLLECT.getKey().equals(interactionType)) {
			lambdaQueryWrapper.eq(PictureInteractionDO::getInteractionType, PictureInteractionTypeEnum.COLLECT.getKey());
		}
		PictureInteractionDO pictureInteractionDO = pictureInteractionPersistenceService.getOne(lambdaQueryWrapper);
		if (pictureInteractionDO == null) {
			pictureInteractionDO = new PictureInteractionDO();
			pictureInteractionDO.setPictureId(pictureId);
			pictureInteractionDO.setUserId(userId);
			pictureInteractionDO.setInteractionType(interactionType);
			return pictureInteractionPersistenceService.save(pictureInteractionDO);
		} else {
			pictureInteractionDO.setInteractionStatus(interactionStatus);
			return pictureInteractionPersistenceService.update(new LambdaUpdateWrapper<PictureInteractionDO>()
					.set(PictureInteractionDO::getInteractionStatus, interactionStatus)
					.eq(PictureInteractionDO::getUserId, userId)
					.eq(PictureInteractionDO::getPictureId, pictureId)
					.eq(PictureInteractionDO::getInteractionType, interactionType)
			);
		}
	}

	/**
	 * 根据图片ID和用户ID获取图片交互数据
	 *
	 * @param pictureId 图片ID
	 * @param userId    用户ID
	 * @return 图片交互数据
	 */
	@Override
	public List<PictureInteraction> getPictureInteractionByPictureIdAndUserId(Long pictureId, Long userId) {
		List<PictureInteractionDO> interactionDOList = pictureInteractionPersistenceService.list(new LambdaQueryWrapper<PictureInteractionDO>()
				.eq(PictureInteractionDO::getPictureId, pictureId)
				.eq(PictureInteractionDO::getUserId, userId)
		);
		return PictureInteractionConverter.toDomainList(interactionDOList);
	}

	/**
	 * 根据图片ID列表和用户ID获取图片交互数据
	 *
	 * @param pictureIds 图片ID列表
	 * @param userId     用户ID
	 * @return 图片交互数据
	 */
	@Override
	public List<PictureInteraction> getPictureInteractionByPictureIdsAndUserId(Set<Long> pictureIds, Long userId) {
		List<PictureInteractionDO> interactionDOList = pictureInteractionPersistenceService.list(new LambdaQueryWrapper<PictureInteractionDO>()
				.in(PictureInteractionDO::getPictureId, pictureIds)
				.eq(PictureInteractionDO::getUserId, userId)
		);
		return PictureInteractionConverter.toDomainList(interactionDOList);
	}
}
