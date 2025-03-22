package com.baolong.pictures.domain.menu.repository;

import com.baolong.pictures.domain.menu.entity.RoleMenu;

import java.util.List;

/**
 * 角色菜单关联表 (role_menu) - 仓储服务接口
 *
 * @author Baolong 2025年03月21 22:38
 * @version 1.0
 * @since 1.8
 */
public interface RoleMenuRepository {

	/**
	 * 根据用户角色获取角色菜单关联列表
	 *
	 * @param userRole 用户角色
	 * @return 角色菜单关联列表
	 */
	List<RoleMenu> getRoleMenuListByUserRole(String userRole);
}
