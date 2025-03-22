package com.baolong.pictures.infrastructure.persistence.menu.roleMenu.converter;

import cn.hutool.core.bean.BeanUtil;
import com.baolong.pictures.domain.menu.entity.RoleMenu;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.menu.roleMenu.mybatis.RoleMenuDO;
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
public class RoleMenuConverter {

	/**
	 * 领域模型 转为 持久化模型
	 *
	 * @param roleMenu 领域模型
	 * @return 持久化模型
	 */
	public static RoleMenuDO toDO(RoleMenu roleMenu) {
		RoleMenuDO roleMenuDO = new RoleMenuDO();
		BeanUtil.copyProperties(roleMenu, roleMenuDO);
		return roleMenuDO;
	}

	/**
	 * 领域模型列表 转为 持久化模型列表
	 */
	public static List<RoleMenuDO> toDOList(List<RoleMenu> roleMenuList) {
		return Optional.ofNullable(roleMenuList)
				.orElse(List.of()).stream()
				.map(RoleMenuConverter::toDO)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型 转为 领域模型
	 *
	 * @param roleMenuDO 持久化模型
	 * @return 领域模型
	 */
	public static RoleMenu toDomain(RoleMenuDO roleMenuDO) {
		RoleMenu roleMenu = new RoleMenu();
		BeanUtil.copyProperties(roleMenuDO, roleMenu);
		return roleMenu;
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 *
	 * @param roleMenuDOList 持久化模型列表
	 * @return 领域模型列表
	 */
	public static List<RoleMenu> toDomainList(List<RoleMenuDO> roleMenuDOList) {
		return Optional.ofNullable(roleMenuDOList)
				.orElse(List.of())
				.stream().map(RoleMenuConverter::toDomain)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 *
	 * @param roleMenuDOPage 持久化模型分页
	 * @return 领域模型分页
	 */
	public static PageVO<RoleMenu> toDomainPage(Page<RoleMenuDO> roleMenuDOPage) {
		return new PageVO<>(
				roleMenuDOPage.getCurrent()
				, roleMenuDOPage.getSize()
				, roleMenuDOPage.getTotal()
				, roleMenuDOPage.getPages()
				, Optional.ofNullable(roleMenuDOPage.getRecords())
				.orElse(List.of())
				.stream().map(RoleMenuConverter::toDomain)
				.collect(Collectors.toList())
		);
	}
}
