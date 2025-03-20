package com.baolong.pictures.infrastructure.persistence.user.converter;

import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.user.mybatis.UserDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模型转化类（领域模型 <=> 持久化模型）
 *
 * @author Baolong 2025年03月20 20:42
 * @version 1.0
 * @since 1.8
 */
public class UserConverter {

	/**
	 * 领域模型 转为 持久化模型
	 *
	 * @param user 领域模型
	 * @return 持久化模型
	 */
	public static UserDO toDO(User user) {
		UserDO userDO = new UserDO();
		BeanUtils.copyProperties(user, userDO);
		return userDO;
	}

	/**
	 * 持久化模型 转为 领域模型
	 *
	 * @param userDO 持久化模型
	 * @return 领域模型
	 */
	public static User toDomain(UserDO userDO) {
		User user = new User();
		BeanUtils.copyProperties(userDO, user);
		return user;
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 *
	 * @param userDOPage 持久化模型分页
	 * @return 领域模型分页
	 */
	public static PageVO<User> toDomainPage(Page<UserDO> userDOPage) {
		List<User> userList = Optional.ofNullable(userDOPage.getRecords())
				.orElse(List.of())
				.stream().map(UserConverter::toDomain)
				.collect(Collectors.toList());
		return new PageVO<>(
				userDOPage.getCurrent()
				, userDOPage.getSize()
				, userDOPage.getTotal()
				, userDOPage.getPages()
				, userList
		);
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 *
	 * @param userDOList 持久化模型列表
	 * @return 领域模型列表
	 */
	public static List<User> toDomainList(List<UserDO> userDOList) {
		return Optional.ofNullable(userDOList)
				.orElse(List.of())
				.stream().map(UserConverter::toDomain)
				.collect(Collectors.toList());
	}
}
