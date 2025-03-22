package com.baolong.pictures.application.service;

import com.baolong.pictures.domain.menu.entity.Menu;
import com.baolong.pictures.domain.menu.service.MenuDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
	 * 根据用户角色获取菜单列表
	 *
	 * @param userRole 用户角色
	 * @return 菜单列表
	 */
	public List<Menu> getMenuListByUserRole(String userRole) {
		return menuDomainService.getMenuListByUserRole(userRole);
	}
}
