package com.baolong.pictures.domain.space.service;

import com.baolong.pictures.domain.space.entity.SpaceUser;
import com.baolong.pictures.interfaces.web.request.space.SpaceUserQueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 空间用户表 (space_user) - 领域服务接口
 */
public interface SpaceUserDomainService extends IService<SpaceUser> {

	// region 增删改相关

	/**
	 * 新增空间用户到空间
	 *
	 * @param spaceUser 空间用户
	 * @return 是否成功
	 */
	Boolean addSpaceUserToSpace(SpaceUser spaceUser);

	/**
	 * 编辑空间用户权限
	 *
	 * @param spaceUser 空间用户
	 * @return 是否成功
	 */
	Boolean editSpaceUserAuth(SpaceUser spaceUser);

	/**
	 * 删除空间用户
	 *
	 * @param spaceUserId 空间用户 ID
	 * @return 是否成功
	 */
	Boolean deleteSpaceUser(Long spaceUserId);

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 根据空间 ID 查询空间用户信息
	 *
	 * @param spaceUserId 空间用户ID
	 * @return 空间用户信息
	 */
	SpaceUser getSpaceUserById(Long spaceUserId);

	/**
	 * 根据空间 ID 和用户 ID 查询空间用户信息
	 *
	 * @param spaceId 空间 ID
	 * @param userId  用户 ID
	 * @return 空间用户信息
	 */
	SpaceUser getSpaceUserBySpaceIdAndUserId(Long spaceId, Long userId);

	/**
	 * 根据空间 ID 查询空间用户分页列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 空间用户分页列表
	 */
	Page<SpaceUser> getSpaceUserPageListBySpaceId(Page<SpaceUser> page, LambdaQueryWrapper<SpaceUser> lambdaQueryWrapper);

	// endregion 查询相关

	// region 其他相关

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param spaceUserQueryRequest 空间用户查询请求
	 * @return 查询条件对象（Lambda）
	 */
	LambdaQueryWrapper<SpaceUser> getLambdaQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

	// endregion 其他相关
}
