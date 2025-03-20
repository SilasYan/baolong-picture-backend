package com.baolong.pictures.infrastructure.persistence.user.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.domain.user.repository.UserRepository;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.user.converter.UserConverter;
import com.baolong.pictures.infrastructure.persistence.user.mybatis.UserDO;
import com.baolong.pictures.infrastructure.persistence.user.mybatis.UserPersistenceService;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 用户表 (user) - 仓储服务实现
 *
 * @author Baolong 2025年03月20 20:45
 * @version 1.0
 * @since 1.8
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
	private final UserPersistenceService userPersistenceService;

	/**
	 * 查询条件对象
	 *
	 * @param user 用户领域对象
	 * @return 查询条件对象
	 */
	private QueryWrapper<UserDO> queryWrapper(User user) {
		QueryWrapper<UserDO> queryWrapper = new QueryWrapper<>();
		Long id = user.getId();
		String userName = user.getUserName();
		String userAccount = user.getUserAccount();
		String userEmail = user.getUserEmail();
		String userPhone = user.getUserPhone();
		String userProfile = user.getUserProfile();
		String userRole = user.getUserRole();
		queryWrapper.eq(ObjUtil.isNotNull(id), "id", id);
		queryWrapper.like(StrUtil.isNotEmpty(userName), "user_name", userName);
		queryWrapper.eq(StrUtil.isNotEmpty(userAccount), "user_account", userAccount);
		queryWrapper.eq(StrUtil.isNotEmpty(userEmail), "user_email", userEmail);
		queryWrapper.eq(StrUtil.isNotEmpty(userPhone), "user_phone", userPhone);
		queryWrapper.like(StrUtil.isNotEmpty(userProfile), "user_profile", userProfile);
		queryWrapper.eq(StrUtil.isNotEmpty(userRole), "user_role", userRole);
		if (user.isMultipleSort()) {
			List<PageRequest.Sort> sorts = user.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, sortField);
				});
			}
		} else {
			PageRequest.Sort sort = user.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, sortField);
			} else {
				queryWrapper.orderByDesc("create_time");
			}
		}
		return queryWrapper;
	}

	/**
	 * 查询条件对象（Lambda）
	 *
	 * @param user 用户领域对象
	 * @return 查询条件对象（Lambda）
	 */
	private LambdaQueryWrapper<UserDO> lambdaQueryWrapper(User user) {
		LambdaQueryWrapper<UserDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		Long id = user.getId();
		String userName = user.getUserName();
		String userAccount = user.getUserAccount();
		String userEmail = user.getUserEmail();
		String userPhone = user.getUserPhone();
		String userProfile = user.getUserProfile();
		String userRole = user.getUserRole();
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(id), UserDO::getId, id);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(userName), UserDO::getUserName, userName);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(userAccount), UserDO::getUserAccount, userAccount);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(userEmail), UserDO::getUserEmail, userEmail);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(userPhone), UserDO::getUserPhone, userPhone);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(userProfile), UserDO::getUserProfile, userProfile);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(userRole), UserDO::getUserRole, userRole);
		// 处理排序规则
		if (user.isMultipleSort()) {
			List<PageRequest.Sort> sorts = user.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(UserDO.class, sortField));
				});
			}
		} else {
			PageRequest.Sort sort = user.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(UserDO.class, sortField));
			} else {
				lambdaQueryWrapper.orderByDesc(UserDO::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	/**
	 * 根据用户邮箱判断用户是否存在
	 *
	 * @param userEmail 用户邮箱
	 * @return 是否存在
	 */
	@Override
	public boolean existedUserByEmail(String userEmail) {
		return userPersistenceService.getBaseMapper()
				.exists(new QueryWrapper<UserDO>()
						.eq("user_email", userEmail)
				);
	}

	/**
	 * 根据用户ID判断用户是否存在
	 *
	 * @param userId 用户ID
	 * @return 是否存在
	 */
	@Override
	public boolean existedUserById(Long userId) {
		return userPersistenceService.getBaseMapper()
				.exists(new QueryWrapper<UserDO>()
						.eq("id", userId)
				);
	}

	/**
	 * 新增用户
	 *
	 * @param user 用户领域对象
	 * @return 是否成功
	 */
	@Override
	public boolean addUser(User user) {
		return userPersistenceService.save(UserConverter.toDO(user));
	}

	/**
	 * 更新用户
	 *
	 * @param user 用户领域对象
	 * @return 是否成功
	 */
	@Override
	public boolean updateUser(User user) {
		return userPersistenceService.updateById(UserConverter.toDO(user));
	}

	/**
	 * 删除用户
	 *
	 * @param userId 用户ID
	 * @return 是否成功
	 */
	@Override
	public boolean deleteUser(Long userId) {
		return userPersistenceService.removeById(userId);
	}

	/**
	 * 根据用户邮箱查询用户
	 *
	 * @param userEmail 用户邮箱
	 * @return 用户领域对象
	 */
	@Override
	public User getUserByUserEmail(String userEmail) {
		UserDO userDO = userPersistenceService.getOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getUserEmail, userEmail));
		if (userDO == null) {
			return null;
		} else {
			return UserConverter.toDomain(userDO);
		}
	}

	/**
	 * 根据用户账号查询用户
	 *
	 * @param userAccount 用户账号
	 * @return 用户领域对象
	 */
	@Override
	public User getUserByUserAccount(String userAccount) {
		UserDO userDO = userPersistenceService.getOne(new LambdaQueryWrapper<UserDO>().eq(UserDO::getUserAccount, userAccount));
		if (userDO == null) {
			return null;
		} else {
			return UserConverter.toDomain(userDO);
		}
	}

	/**
	 * 根据用户ID获取用户
	 *
	 * @param userId 用户ID
	 * @return 用户领域对象
	 */
	@Override
	public User getUserByUserId(Long userId) {
		UserDO userDO = userPersistenceService.getById(userId);
		if (userDO == null) {
			return null;
		}
		return UserConverter.toDomain(userDO);
	}

	/**
	 * 获取用户分页列表
	 *
	 * @param user 用户领域对象
	 * @return 用户领域对象分页列表
	 */
	@Override
	public PageVO<User> getUserPageList(User user) {
		LambdaQueryWrapper<UserDO> lambdaQueryWrapper = this.lambdaQueryWrapper(user);
		Page<UserDO> page = userPersistenceService.page(user.getPage(UserDO.class), lambdaQueryWrapper);
		return UserConverter.toDomainPage(page);
	}

	/**
	 * 根据用户ID集合获取用户列表
	 *
	 * @param userIds 用户ID集合
	 * @return 用户列表
	 */
	@Override
	public List<User> getUserListByUserIds(Set<Long> userIds) {
		List<UserDO> userDOList = userPersistenceService.list(new LambdaQueryWrapper<UserDO>().in(UserDO::getId, userIds));
		return UserConverter.toDomainList(userDOList);
	}
}
