package com.baolong.pictures.infrastructure.persistence.space.space.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.domain.space.aggregate.enums.SpaceTypeEnum;
import com.baolong.pictures.domain.space.repository.SpaceRepository;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.space.space.converter.SpaceConverter;
import com.baolong.pictures.infrastructure.persistence.space.space.mybatis.SpaceDO;
import com.baolong.pictures.infrastructure.persistence.space.space.mybatis.SpacePersistenceService;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 空间表 (space) - 仓储服务实现
 *
 * @author Baolong 2025年03月21 00:25
 * @version 1.0
 * @since 1.8
 */
@Repository
@RequiredArgsConstructor
public class SpaceRepositoryImpl implements SpaceRepository {

	private final SpacePersistenceService spacePersistenceService;

	/**
	 * 查询条件对象（Lambda）
	 *
	 * @param space 空间领域对象
	 * @return 查询条件对象（Lambda）
	 */
	private LambdaQueryWrapper<SpaceDO> lambdaQueryWrapper(Space space) {
		LambdaQueryWrapper<SpaceDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		Long id = space.getSpaceId();
		String spaceName = space.getSpaceName();
		Integer spaceType = space.getSpaceType();
		Integer spaceLevel = space.getSpaceLevel();
		Long userId = space.getUserId();
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(id), SpaceDO::getId, id);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(spaceName), SpaceDO::getSpaceName, spaceName);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(spaceType), SpaceDO::getSpaceType, spaceType);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(spaceLevel), SpaceDO::getSpaceLevel, spaceLevel);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(userId), SpaceDO::getUserId, userId);
		// 处理排序规则
		if (space.isMultipleSort()) {
			List<PageRequest.Sort> sorts = space.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(
							StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(SpaceDO.class, sortField)
					);
				});
			}
		} else {
			PageRequest.Sort sort = space.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(
						StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(SpaceDO.class, sortField)
				);
			} else {
				lambdaQueryWrapper.orderByDesc(SpaceDO::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	/**
	 * 根据用户ID判断个人空间是否存在
	 *
	 * @param userId 用户ID
	 * @return 是否存在
	 */
	@Override
	public boolean existedPersonSpaceByUserId(Long userId) {
		return spacePersistenceService.getBaseMapper()
				.exists(new LambdaQueryWrapper<SpaceDO>()
						.eq(SpaceDO::getUserId, userId)
						.eq(SpaceDO::getSpaceType, SpaceTypeEnum.PRIVATE.getKey())
				);
	}

	/**
	 * 根据空间ID判断空间是否存在
	 *
	 * @param spaceId 空间ID
	 * @return 是否存在
	 */
	@Override
	public boolean existedSpaceBySpaceId(Long spaceId) {
		return spacePersistenceService.getBaseMapper()
				.exists(new LambdaQueryWrapper<SpaceDO>()
						.eq(SpaceDO::getId, spaceId)
				);
	}

	/**
	 * 新增空间
	 *
	 * @param space 空间领域模型
	 * @return 是否成功
	 */
	@Override
	public boolean addSpace(Space space) {
		return spacePersistenceService.save(SpaceConverter.toDO(space));
	}

	/**
	 * 更新空间
	 *
	 * @param space 空间领域模型
	 * @return 是否成功
	 */
	@Override
	public boolean updateSpace(Space space) {
		return spacePersistenceService.updateById(SpaceConverter.toDO(space));
	}

	/**
	 * 删除空间
	 *
	 * @param spaceId 空间ID
	 * @return 是否成功
	 */
	@Override
	public boolean deleteSpace(Long spaceId) {
		return spacePersistenceService.removeById(spaceId);
	}

	/**
	 * 更新空间大小和数量
	 *
	 * @param spaceId  空间 ID
	 * @param picSize  图片大小
	 * @param picCount 图片数量
	 * @return 是否成功
	 */
	@Override
	public boolean updateSpaceSizeAndCount(Long spaceId, Long picSize, Long picCount) {
		return spacePersistenceService.update(new LambdaUpdateWrapper<SpaceDO>()
				.eq(SpaceDO::getId, spaceId)
				.setSql("used_size = used_size + (" + picSize + ")")
				.setSql("used_count = used_count + (" + picCount + ")")
		);
	}

	/**
	 * 根据空间ID获取空间
	 *
	 * @param spaceId 空间ID
	 * @return 空间领域模型
	 */
	@Override
	public Space getSpaceBySpaceId(Long spaceId) {
		SpaceDO spaceDO = spacePersistenceService.getById(spaceId);
		if (spaceDO == null) {
			return null;
		}
		return SpaceConverter.toDomain(spaceDO);
	}

	/**
	 * 根据用户ID获取个人空间
	 *
	 * @param userId 用户ID
	 * @return 空间领域对象
	 */
	@Override
	public Space getPersonSpaceByUserId(Long userId) {
		SpaceDO spaceDO = spacePersistenceService.getOne(new LambdaQueryWrapper<SpaceDO>()
				.eq(SpaceDO::getUserId, userId)
				.eq(SpaceDO::getSpaceType, SpaceTypeEnum.PRIVATE.getKey())
		);
		if (spaceDO == null) {
			return null;
		}
		return SpaceConverter.toDomain(spaceDO);
	}

	/**
	 * 获取空间分页列表
	 *
	 * @param space 空间领域对象
	 * @return 空间领域对象分页列表
	 */
	@Override
	public PageVO<Space> getSpacePageList(Space space) {
		LambdaQueryWrapper<SpaceDO> lambdaQueryWrapper = this.lambdaQueryWrapper(space);
		Page<SpaceDO> page = spacePersistenceService.page(space.getPage(SpaceDO.class), lambdaQueryWrapper);
		return SpaceConverter.toDomainPage(page);
	}

	/**
	 * 根据空间ID列表获取空间列表
	 *
	 * @param spaceIds 空间列表
	 * @return 空间列表
	 */
	@Override
	public List<Space> getSpaceListBySpaceIdList(Set<Long> spaceIds) {
		List<SpaceDO> spaceDOList = spacePersistenceService.list(new LambdaQueryWrapper<SpaceDO>()
				.in(SpaceDO::getId, spaceIds)
		);
		return SpaceConverter.toDomainList(spaceDOList);
	}
}
