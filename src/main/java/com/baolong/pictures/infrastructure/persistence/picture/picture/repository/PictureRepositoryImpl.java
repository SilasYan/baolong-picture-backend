package com.baolong.pictures.infrastructure.persistence.picture.picture.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.picture.aggregate.Picture;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureExpandStatusEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureInteractionTypeEnum;
import com.baolong.pictures.domain.picture.repository.PictureRepository;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.picture.picture.converter.PictureConverter;
import com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis.PictureDO;
import com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis.PicturePersistenceService;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Repository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 图片表 (picture) - 仓储服务实现
 *
 * @author Baolong 2025年03月20 23:24
 * @version 1.0
 * @since 1.8
 */
@Repository
@RequiredArgsConstructor
public class PictureRepositoryImpl implements PictureRepository {

	private final PicturePersistenceService picturePersistenceService;

	/**
	 * 查询条件对象（Lambda）
	 *
	 * @param picture 图片查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@SneakyThrows
	private LambdaQueryWrapper<PictureDO> lambdaQueryWrapper(Picture picture) {
		LambdaQueryWrapper<PictureDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		String searchText = picture.getSearchText();
		Long id = picture.getPictureId();
		String originFormat = picture.getOriginFormat();
		Integer originWidth = picture.getOriginWidth();
		Integer originHeight = picture.getOriginHeight();
		Double originScale = picture.getOriginScale();
		String originColor = picture.getOriginColor();
		String picName = picture.getPicName();
		String picDesc = picture.getPicDesc();
		Long category = picture.getCategoryId();
		String tags = picture.getTags();
		Long userId = picture.getUserId();
		Long spaceId = picture.getSpaceId();
		Integer reviewStatus = picture.getReviewStatus();
		String reviewMessage = picture.getReviewMessage();
		Long reviewerUser = picture.getReviewerUser();
		Integer isShare = picture.getIsShare();
		Integer expandStatus = picture.getExpandStatus();
		lambdaQueryWrapper.and(StrUtil.isNotEmpty(searchText), lqw ->
				lqw.like(PictureDO::getPicName, searchText)
						.or().like(PictureDO::getPicDesc, searchText)
						.or().apply("FIND_IN_SET ('" + searchText + "', tags) > 0")
		);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(id), PictureDO::getId, id);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(originFormat), PictureDO::getOriginFormat, originFormat);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(originWidth), PictureDO::getOriginWidth, originWidth);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(originHeight), PictureDO::getOriginHeight, originHeight);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(originScale), PictureDO::getOriginScale, originScale);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(originColor), PictureDO::getOriginColor, originColor);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(picName), PictureDO::getPicName, picName);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(picDesc), PictureDO::getPicDesc, picDesc);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(category), PictureDO::getCategoryId, category);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(userId), PictureDO::getUserId, userId);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(spaceId), PictureDO::getSpaceId, spaceId);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(reviewStatus), PictureDO::getReviewStatus, reviewStatus);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(reviewMessage), PictureDO::getReviewMessage, reviewMessage);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(reviewerUser), PictureDO::getReviewerUser, reviewerUser);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(isShare), PictureDO::getIsShare, isShare);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(expandStatus), PictureDO::getExpandStatus, expandStatus);
		if (StrUtil.isNotEmpty(picture.getStartEditTime()) && StrUtil.isNotEmpty(picture.getEndEditTime())) {
			Date startEditTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(picture.getStartEditTime());
			Date endEditTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(picture.getEndEditTime());
			lambdaQueryWrapper.ge(ObjUtil.isNotEmpty(startEditTime), PictureDO::getEditTime, startEditTime);
			lambdaQueryWrapper.lt(ObjUtil.isNotEmpty(endEditTime), PictureDO::getEditTime, endEditTime);
		}
		// 是否查询扩图图片的处理
		Boolean expandQuery = picture.getExpandQuery();
		if (expandQuery) {
			lambdaQueryWrapper.or().eq(PictureDO::getExpandStatus, PictureExpandStatusEnum.YES_SUCCESS.getKey());
		}
		// 处理排序规则
		if (picture.isMultipleSort()) {
			List<PageRequest.Sort> sorts = picture.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(
							StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(PictureDO.class, sortField)
					);
				});
			}
		} else {
			PageRequest.Sort sort = picture.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(
						StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(PictureDO.class, sortField)
				);
			} else {
				lambdaQueryWrapper.orderByDesc(PictureDO::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	/**
	 * 新增图片
	 *
	 * @param picture 图片领域对象
	 * @return 图片ID
	 */
	@Override
	public Long addPicture(Picture picture) {
		PictureDO pictureDO = PictureConverter.toDO(picture);
		picturePersistenceService.save(pictureDO);
		return pictureDO.getId();
	}

	/**
	 * 更新图片
	 *
	 * @param picture 图片领域对象
	 * @return 图片ID
	 */
	@Override
	public Long updatePicture(Picture picture) {
		PictureDO pictureDO = PictureConverter.toDO(picture);
		picturePersistenceService.updateById(pictureDO);
		return pictureDO.getId();
	}

	/**
	 * 删除图片
	 *
	 * @param pictureId 图片 ID
	 * @return 是否成功
	 */
	@Override
	public boolean deletePicture(Long pictureId) {
		return picturePersistenceService.removeById(pictureId);
	}

	/**
	 * 更新互动数量
	 *
	 * @param pictureId       图片 ID
	 * @param interactionType 互动类型
	 * @param num             变更数量
	 * @return 是否成功
	 */
	@Override
	public boolean updateInteractionNum(Long pictureId, Integer interactionType, int num) {
		LambdaUpdateWrapper<PictureDO> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(PictureDO::getId, pictureId);
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
		return picturePersistenceService.update(updateWrapper);
	}

	/**
	 * 批量更新图片
	 *
	 * @param pictureList 图片领域对象列表
	 * @return 是否成功
	 */
	@Override
	public boolean updatePictureByBatch(List<Picture> pictureList) {
		return picturePersistenceService.updateBatchById(PictureConverter.toDOList(pictureList));
	}

	/**
	 * 根据图片ID判断图片是否存在
	 *
	 * @param pictureId 图片ID
	 * @return 是否存在
	 */
	@Override
	public boolean existedPictureByPictureId(Long pictureId) {
		return picturePersistenceService.getBaseMapper()
				.exists(new LambdaQueryWrapper<PictureDO>()
						.eq(PictureDO::getId, pictureId)
				);
	}

	/**
	 * 根据图片ID和用户ID判断图片是否存在
	 *
	 * @param pictureId 图片ID
	 * @param userId    用户ID
	 * @return 是否存在
	 */
	@Override
	public boolean existedPictureByPictureIdAndUserId(Long pictureId, Long userId) {
		return picturePersistenceService.getBaseMapper()
				.exists(new LambdaQueryWrapper<PictureDO>()
						.eq(PictureDO::getId, pictureId)
						.eq(PictureDO::getUserId, userId)
				);
	}

	/**
	 * 根据图片ID获取图片
	 *
	 * @param pictureId 图片ID
	 * @return 图片领域对象
	 */
	@Override
	public Picture getPictureByPictureId(Long pictureId) {
		PictureDO pictureDO = picturePersistenceService.getById(pictureId);
		if (pictureDO == null) {
			return null;
		}
		return PictureConverter.toDomain(pictureDO);
	}

	/**
	 * 获取图片分页列表
	 *
	 * @param picture 图片领域对象
	 * @return 图片领域对象分页列表
	 */
	@Override
	public PageVO<Picture> getPicturePageList(Picture picture) {
		LambdaQueryWrapper<PictureDO> lambdaQueryWrapper = this.lambdaQueryWrapper(picture);
		Page<PictureDO> page = picturePersistenceService.page(picture.getPage(PictureDO.class), lambdaQueryWrapper);
		return PictureConverter.toDomainPage(page);
	}

	/**
	 * 根据图片ID集合获取图片列表
	 *
	 * @param pictureIds 图片ID集合
	 * @return 图片列表
	 */
	@Override
	public List<Picture> getPictureByPictureIds(Set<Long> pictureIds) {
		List<PictureDO> pictureDOList = picturePersistenceService.list(new LambdaQueryWrapper<PictureDO>().in(PictureDO::getId, pictureIds));
		return PictureConverter.toDomainList(pictureDOList);
	}
}
