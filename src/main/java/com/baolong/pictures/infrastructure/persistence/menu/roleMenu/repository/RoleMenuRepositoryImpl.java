package com.baolong.pictures.infrastructure.persistence.menu.roleMenu.repository;

import com.baolong.pictures.domain.menu.entity.RoleMenu;
import com.baolong.pictures.domain.menu.repository.RoleMenuRepository;
import com.baolong.pictures.infrastructure.persistence.menu.roleMenu.converter.RoleMenuConverter;
import com.baolong.pictures.infrastructure.persistence.menu.roleMenu.mybatis.RoleMenuDO;
import com.baolong.pictures.infrastructure.persistence.menu.roleMenu.mybatis.RoleMenuPersistenceService;
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
}
