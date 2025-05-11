package com.baolong.pictures.domain.space.repository;

import com.baolong.pictures.domain.space.aggregate.SpaceUser;

import java.util.List;

/**
 * 空间用户表 (space_user) - 仓储服务接口
 *
 * @author Baolong 2025年03月21 09:43
 * @version 1.0
 * @since 1.8
 */
public interface SpaceUserRepository {

	/**
	 * 根据空间ID和用户ID查询空间用户
	 *
	 * @param spaceId 空间 ID
	 * @param userId  用户 ID
	 * @return 空间用户领域对象
	 */
	SpaceUser getSpaceUserBySpaceIdAndUserId(Long spaceId, Long userId);

	/**
	 * 根据用户ID获取团队空间列表
	 *
	 * @param userId 用户ID
	 * @return 团队空间列表
	 */
	List<SpaceUser> getTeamSpaceListByUserId(Long userId);

	/**
	 * 添加空间用户
	 *
	 * @param spaceUser 空间用户领域对象
	 * @return 是否成功
	 */
	boolean addSpaceUser(SpaceUser spaceUser);

	/**
	 * 修改用户在空间的权限
	 *
	 * @param spaceUser 空间用户领域对象
	 */
	boolean updateSpaceUserRole(SpaceUser spaceUser);

	/**
	 * 获取空间用户列表
	 *
	 * @param spaceUser 空间用户领域对象
	 * @return 空间用户列表
	 */
	List<SpaceUser> getSpaceUserList(SpaceUser spaceUser);

	/**
	 * 删除空间用户
	 *
	 * @param spaceUserId 空间用户ID
	 */
	boolean deleteSpaceUser(Long spaceUserId);
}
