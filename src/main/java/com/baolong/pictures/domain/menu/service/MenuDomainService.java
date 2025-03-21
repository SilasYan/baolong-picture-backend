package com.baolong.pictures.domain.menu.service;

import cn.hutool.core.collection.CollUtil;
import com.baolong.pictures.domain.menu.entity.Menu;
import com.baolong.pictures.domain.menu.entity.RoleMenu;
import com.baolong.pictures.domain.menu.repository.MenuRepository;
import com.baolong.pictures.domain.menu.repository.RoleMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 菜单表 (menu) - 领域服务
 *
 * @author Baolong 2025-03-19 20:21:10
 */
@Service
@RequiredArgsConstructor
public class MenuDomainService {

	private final MenuRepository menuRepository;
	private final RoleMenuRepository roleMenuRepository;

	/**
	 * 根据用户角色获取菜单列表
	 *
	 * @param userRole 用户角色
	 * @return 菜单列表
	 */
	public List<Menu> getMenuListByUserRole(String userRole) {
		List<RoleMenu> roleMenu = roleMenuRepository.getRoleMenuListByUserRole(userRole);
		if (CollUtil.isNotEmpty(roleMenu)) {
			Set<Long> menuIdList = roleMenu.stream().map(RoleMenu::getMenuId).collect(Collectors.toSet());
			return menuRepository.getMenuListByMenuIdList(menuIdList);
		}
		return List.of();
	}
}
