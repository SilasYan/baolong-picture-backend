package com.baolong.pictures.domain.space.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.space.entity.SpaceUser;
import com.baolong.pictures.domain.space.service.SpaceUserDomainService;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.repository.SpaceUserRepository;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baolong.pictures.interfaces.dto.space.SpaceUserQueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 空间用户表 (space_user) - 领域服务实现
 */
@Service
public class SpaceUserDomainServiceImpl extends ServiceImpl<SpaceUserRepository, SpaceUser> implements SpaceUserDomainService {

	// region 增删改相关

	/**
	 * 新增空间用户到空间
	 *
	 * @param spaceUser 空间用户
	 * @return 是否成功
	 */
	@Override
	public Boolean addSpaceUserToSpace(SpaceUser spaceUser) {
		return this.save(spaceUser);
	}

	/**
	 * 编辑空间用户权限
	 *
	 * @param spaceUser 空间用户
	 * @return 是否成功
	 */
	@Override
	public Boolean editSpaceUserAuth(SpaceUser spaceUser) {
		return this.updateById(spaceUser);
	}

	/**
	 * 删除空间用户
	 *
	 * @param spaceUserId 空间用户 ID
	 * @return 是否成功
	 */
	@Override
	public Boolean deleteSpaceUser(Long spaceUserId) {
		return this.removeById(spaceUserId);
	}

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 根据空间 ID 查询空间用户信息
	 *
	 * @param spaceUserId 空间用户ID
	 * @return 空间用户信息
	 */
	@Override
	public SpaceUser getSpaceUserById(Long spaceUserId) {
		return this.getById(spaceUserId);
	}

	/**
	 * 根据空间 ID 和用户 ID 查询空间用户信息
	 *
	 * @param spaceId 空间 ID
	 * @param userId  用户 ID
	 * @return 空间用户信息
	 */
	@Override
	public SpaceUser getSpaceUserBySpaceIdAndUserId(Long spaceId, Long userId) {
		return this.getOne(new LambdaQueryWrapper<SpaceUser>()
				.eq(SpaceUser::getSpaceId, spaceId).eq(SpaceUser::getUserId, userId)
		);
	}

	/**
	 * 根据空间 ID 查询空间用户分页列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 空间用户分页列表
	 */
	@Override
	public Page<SpaceUser> getSpaceUserPageListBySpaceId(Page<SpaceUser> page, LambdaQueryWrapper<SpaceUser> lambdaQueryWrapper) {
		return this.page(page, lambdaQueryWrapper);
	}

	// endregion 查询相关

	// region 其他相关

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param spaceUserQueryRequest 空间用户查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@Override
	public LambdaQueryWrapper<SpaceUser> getLambdaQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
		LambdaQueryWrapper<SpaceUser> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		Long id = spaceUserQueryRequest.getId();
		Long spaceId = spaceUserQueryRequest.getSpaceId();
		Long userId = spaceUserQueryRequest.getUserId();
		String spaceRole = spaceUserQueryRequest.getSpaceRole();
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(id), SpaceUser::getId, id);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(spaceId), SpaceUser::getSpaceId, spaceId);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(userId), SpaceUser::getUserId, userId);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(spaceRole), SpaceUser::getSpaceRole, spaceRole);
		// 处理排序规则
		if (spaceUserQueryRequest.isMultipleSort()) {
			List<PageRequest.Sort> sorts = spaceUserQueryRequest.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(
							StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(SpaceUser.class, sortField)
					);
				});
			}
		} else {
			PageRequest.Sort sort = spaceUserQueryRequest.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(
						StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(SpaceUser.class, sortField)
				);
			} else {
				lambdaQueryWrapper.orderByDesc(SpaceUser::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	// endregion 其他相关
}




