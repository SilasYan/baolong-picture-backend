package com.baolong.pictures.domain.user.repository;

import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.common.page.PageVO;

import java.util.List;
import java.util.Set;

/**
 * 用户表 (picture) - 仓储服务接口
 *
 * @author Baolong 2025年03月20 20:44
 * @version 1.0
 * @since 1.8
 */
public interface UserRepository {

	/**
	 * 根据用户邮箱判断用户是否存在
	 *
	 * @param userEmail 用户邮箱
	 * @return 是否存在
	 */
	boolean existedUserByEmail(String userEmail);

	/**
	 * 根据用户ID判断用户是否存在
	 *
	 * @param userId 用户ID
	 * @return 是否存在
	 */
	boolean existedUserById(Long userId);

	/**
	 * 新增用户
	 *
	 * @param user 用户领域对象
	 * @return 是否成功
	 */
	boolean addUser(User user);

	/**
	 * 更新用户
	 *
	 * @param user 用户领域对象
	 * @return 是否成功
	 */
	boolean updateUser(User user);

	/**
	 * 删除用户
	 *
	 * @param userId 用户ID
	 * @return 是否成功
	 */
	boolean deleteUser(Long userId);

	/**
	 * 根据用户邮箱查询用户
	 *
	 * @param userEmail 用户邮箱
	 * @return 用户领域对象
	 */
	User getUserByUserEmail(String userEmail);

	/**
	 * 根据用户账号查询用户
	 *
	 * @param userAccount 用户账号
	 * @return 用户领域对象
	 */
	User getUserByUserAccount(String userAccount);

	/**
	 * 根据用户ID获取用户
	 *
	 * @param userId 用户ID
	 * @return 用户领域对象
	 */
	User getUserByUserId(Long userId);

	/**
	 * 获取用户分页列表
	 *
	 * @param user 用户领域对象
	 * @return 用户领域对象分页列表
	 */
	PageVO<User> getUserPageList(User user);

	/**
	 * 根据用户ID集合获取用户列表
	 *
	 * @param userIds 用户ID集合
	 * @return 用户列表
	 */
	List<User> getUserListByUserIds(Set<Long> userIds);
}
