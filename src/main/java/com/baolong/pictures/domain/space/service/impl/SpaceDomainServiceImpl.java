package com.baolong.pictures.domain.space.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.space.entity.Space;
import com.baolong.pictures.domain.space.service.SpaceDomainService;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.repository.SpaceRepository;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baolong.pictures.interfaces.dto.space.SpaceQueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 空间表 (space) - 领域服务实现
 */
@Service
public class SpaceDomainServiceImpl extends ServiceImpl<SpaceRepository, Space> implements SpaceDomainService {

	// region 其他相关

	/**
	 * 根据用户 ID 和空间类型判断空间是否存在
	 *
	 * @param userId    用户 ID
	 * @param spaceType 空间类型
	 * @return 是否存在
	 */
	@Override
	public Boolean existSpaceByUserIdAndSpaceType(Long userId, Integer spaceType) {
		return this.getBaseMapper().exists(new LambdaQueryWrapper<Space>()
				.eq(Space::getUserId, userId)
				.eq(Space::getSpaceType, spaceType)
		);
	}

	/**
	 * 根据空间 ID 判断空间是否存在
	 *
	 * @param spaceId 空间 ID
	 * @return 是否存在
	 */
	@Override
	public Boolean existSpaceById(Long spaceId) {
		return this.getBaseMapper().exists(new LambdaQueryWrapper<Space>().eq(Space::getId, spaceId));
	}

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param spaceQueryRequest 空间查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@Override
	public LambdaQueryWrapper<Space> getLambdaQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
		LambdaQueryWrapper<Space> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		Long id = spaceQueryRequest.getId();
		String spaceName = spaceQueryRequest.getSpaceName();
		Integer spaceType = spaceQueryRequest.getSpaceType();
		Integer spaceLevel = spaceQueryRequest.getSpaceLevel();
		Long userId = spaceQueryRequest.getUserId();
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(id), Space::getId, id);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(spaceName), Space::getSpaceName, spaceName);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(spaceType), Space::getSpaceType, spaceType);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(spaceLevel), Space::getSpaceLevel, spaceLevel);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(userId), Space::getUserId, userId);
		// 处理排序规则
		if (spaceQueryRequest.isMultipleSort()) {
			List<PageRequest.Sort> sorts = spaceQueryRequest.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(
							StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(Space.class, sortField)
					);
				});
			}
		} else {
			PageRequest.Sort sort = spaceQueryRequest.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(
						StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(Space.class, sortField)
				);
			} else {
				lambdaQueryWrapper.orderByDesc(Space::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	// endregion 其他相关

	// region 增删改相关

	/**
	 * 创建空间
	 *
	 * @param space 空间对象
	 * @return 是否成功
	 */
	@Override
	public Boolean addSpace(Space space) {
		return this.save(space);
	}

	/**
	 * 删除空间
	 *
	 * @param spaceId 空间ID
	 * @return 是否成功
	 */
	@Override
	public Boolean deleteSpace(Long spaceId) {
		return this.removeById(spaceId);
	}

	/**
	 * 更新空间
	 *
	 * @param space 空间对象
	 * @return 是否成功
	 */
	@Override
	public Boolean updateSpace(Space space) {
		return this.updateById(space);
	}

	/**
	 * 更新空间大小和数量
	 *
	 * @param updateWrapper 更新条件
	 * @return 是否成功
	 */
	@Override
	public Boolean updateSpaceSizeAndCount(LambdaUpdateWrapper<Space> updateWrapper) {
		return this.update(updateWrapper);
	}

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 根据空间 ID 获取空间信息
	 *
	 * @param spaceId 空间 ID
	 * @return 空间信息
	 */
	@Override
	public Space getSpaceById(Long spaceId) {
		return this.getById(spaceId);
	}

	/**
	 * 获取用户空间列表
	 *
	 * @param lambdaQueryWrapper 查询条件
	 * @return 空间列表
	 */
	@Override
	public List<Space> getSpaceListAsUser(LambdaQueryWrapper<Space> lambdaQueryWrapper) {
		return this.list(lambdaQueryWrapper);
	}

	/**
	 * 获取空间管理分页列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 空间管理分页列表
	 */
	@Override
	public Page<Space> getSpacePageListAsManage(Page<Space> page, LambdaQueryWrapper<Space> lambdaQueryWrapper) {
		return this.page(page, lambdaQueryWrapper);
	}

	// endregion 查询相关
}




