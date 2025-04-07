package com.baolong.pictures.domain.system.menu.service;

import cn.hutool.core.collection.CollUtil;
import com.baolong.pictures.domain.system.menu.aggregate.Menu;
import com.baolong.pictures.domain.system.menu.aggregate.RoleMenu;
import com.baolong.pictures.domain.system.menu.repository.MenuRepository;
import com.baolong.pictures.domain.system.menu.repository.RoleMenuRepository;
import com.baolong.pictures.domain.user.aggregate.enums.UserRoleEnum;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageVO;
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
	 * 添加菜单
	 *
	 * @param menu 菜单领域对象
	 */
	public void addMenu(Menu menu) {
		boolean existed = menuRepository.existedMenuByMenuPath(menu.getMenuPath());
		if (existed) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "菜单路径已存在");
		}
		Long menuId = menuRepository.addMenu(menu);
		if (menuId == null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加菜单失败");
		}
		// 无论什么菜单, 管理员都应该加入
		boolean result = roleMenuRepository.addRoleMenu(menuId, UserRoleEnum.ADMIN.getKey());
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加菜单失败");
	}

	/**
	 * 删除菜单
	 *
	 * @param menuId 菜单ID
	 */
	public void deleteMenu(Long menuId) {
		boolean existed = menuRepository.existedMenuByMenuId(menuId);
		if (existed) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "菜单路径已存在");
		}
		boolean result = menuRepository.deleteMenu(menuId);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除菜单失败");
	}

	/**
	 * 更新菜单
	 *
	 * @param menu 菜单领域对象
	 */
	public void updateMenu(Menu menu) {
		boolean existed = menuRepository.existedMenuByMenuId(menu.getMenuId());
		if (!existed) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "菜单路径不存在");
		}
		Menu oldMenu = menuRepository.getMenuByMenuPath(menu.getMenuPath());
		if (oldMenu != null && !oldMenu.getMenuId().equals(menu.getMenuId())) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "菜单已存在");
		}
		boolean result = menuRepository.updateMenu(menu);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新菜单失败");
	}

	/**
	 * 获取菜单管理分页列表
	 *
	 * @param menu 菜单领域对象
	 * @return 菜单管理分页列表
	 */
	public PageVO<Menu> getMenuPageList(Menu menu) {
		return menuRepository.getMenuPageList(menu);
	}

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
