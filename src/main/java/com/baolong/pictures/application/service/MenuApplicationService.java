package com.baolong.pictures.application.service;

import com.baolong.pictures.domain.system.menu.aggregate.Menu;
import com.baolong.pictures.domain.system.menu.service.MenuDomainService;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 菜单表 (menu) - 应用服务
 *
 * @author Baolong 2025年03月21 22:46
 * @version 1.0
 * @since 1.8
 */
@Service
@RequiredArgsConstructor
public class MenuApplicationService {

	private final MenuDomainService menuDomainService;

	/**
	 * 添加菜单
	 *
	 * @param menu 菜单领域对象
	 */
	@Transactional(rollbackFor = Exception.class)
	public void addMenu(Menu menu) {
		menuDomainService.addMenu(menu);
	}

	/**
	 * 删除菜单
	 *
	 * @param menuId 菜单ID
	 */
	public void deleteMenu(Long menuId) {
		menuDomainService.deleteMenu(menuId);
	}

	/**
	 * 更新菜单
	 *
	 * @param menu 菜单领域对象
	 */
	public void updateMenu(Menu menu) {
		menuDomainService.updateMenu(menu);
	}

	/**
	 * 获取菜单管理分页列表
	 *
	 * @param menu 菜单领域对象
	 * @return 菜单管理分页列表
	 */
	public PageVO<Menu> getMenuPageList(Menu menu) {
		return menuDomainService.getMenuPageList(menu);
	}

	/**
	 * 根据用户角色获取菜单列表
	 *
	 * @param userRole 用户角色
	 * @return 菜单列表
	 */
	public List<Menu> getMenuListByUserRole(String userRole) {
		return menuDomainService.getMenuListByUserRole(userRole);
	}
}
