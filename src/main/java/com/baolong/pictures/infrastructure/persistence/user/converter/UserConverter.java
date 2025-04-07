package com.baolong.pictures.infrastructure.persistence.user.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.user.mybatis.UserDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

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

	private final static CopyOptions toDoOption = CopyOptions.create();
	private final static CopyOptions toDomainOption = CopyOptions.create();

	static {
		toDoOption.setFieldMapping(MapUtil.of("userId", "id"));
		toDomainOption.setFieldMapping(MapUtil.of("id", "userId"));
	}

	/**
	 * 领域模型 转为 持久化模型
	 *
	 * @param user 领域模型
	 * @return 持久化模型
	 */
	public static UserDO toDO(User user) {
		UserDO userDO = new UserDO();
		BeanUtil.copyProperties(user, userDO, toDoOption);
		return userDO;
	}

	/**
	 * 领域模型列表 转为 持久化模型列表
	 */
	public static List<UserDO> toDOList(List<User> userList) {
		return Optional.ofNullable(userList)
				.orElse(List.of()).stream()
				.map(UserConverter::toDO)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型 转为 领域模型
	 *
	 * @param userDO 持久化模型
	 * @return 领域模型
	 */
	public static User toDomain(UserDO userDO) {
		User user = new User();
		BeanUtil.copyProperties(userDO, user, toDomainOption);
		return user;
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

	/**
	 * 持久化模型分页 转为 领域模型分页
	 *
	 * @param userDOPage 持久化模型分页
	 * @return 领域模型分页
	 */
	public static PageVO<User> toDomainPage(Page<UserDO> userDOPage) {
		return new PageVO<>(
				userDOPage.getCurrent()
				, userDOPage.getSize()
				, userDOPage.getTotal()
				, userDOPage.getPages()
				, Optional.ofNullable(userDOPage.getRecords())
				.orElse(List.of())
				.stream().map(UserConverter::toDomain)
				.collect(Collectors.toList())
		);
	}
}
