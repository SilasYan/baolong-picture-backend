package com.baolong.pictures.infrastructure.persistence.system.menu.roleMenu.repository;

import com.baolong.pictures.domain.system.menu.aggregate.RoleMenu;
import com.baolong.pictures.domain.system.menu.repository.RoleMenuRepository;
import com.baolong.pictures.infrastructure.persistence.system.menu.roleMenu.converter.RoleMenuConverter;
import com.baolong.pictures.infrastructure.persistence.system.menu.roleMenu.mybatis.RoleMenuDO;
import com.baolong.pictures.infrastructure.persistence.system.menu.roleMenu.mybatis.RoleMenuPersistenceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 角色菜单关联表 (role_menu) - 仓储服务实现
 *
 * @author Baolong 2025年03月21 22:44
 * @version 1.0
 * @since 1.8
 */
@Repository
@RequiredArgsConstructor
public class RoleMenuRepositoryImpl implements RoleMenuRepository {

	private final RoleMenuPersistenceService roleMenuPersistenceService;

	/**
	 * 根据用户角色获取角色菜单关联列表
	 *
	 * @param userRole 用户角色
	 * @return 角色菜单关联列表
	 */
	@Override
	public List<RoleMenu> getRoleMenuListByUserRole(String userRole) {
		List<RoleMenuDO> menuDOList = roleMenuPersistenceService.list(new LambdaQueryWrapper<RoleMenuDO>().eq(RoleMenuDO::getRoleKey, userRole));
		return RoleMenuConverter.toDomainList(menuDOList);
	}

	/**
	 * 新增用户角色和菜单关联关系
	 *
	 * @param menuId   菜单ID
	 * @param userRole 用户角色
	 * @return 是否成功
	 */
	@Override
	public boolean addRoleMenu(Long menuId, String userRole) {
		RoleMenuDO roleMenuDO = new RoleMenuDO();
		roleMenuDO.setMenuId(menuId);
		roleMenuDO.setRoleKey(userRole);
		return roleMenuPersistenceService.save(roleMenuDO);
	}
}
