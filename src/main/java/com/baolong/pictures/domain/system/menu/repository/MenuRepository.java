package com.baolong.pictures.domain.system.menu.repository;

import com.baolong.pictures.domain.system.menu.aggregate.Menu;
import com.baolong.pictures.infrastructure.common.page.PageVO;

import java.util.List;
import java.util.Set;

/**
 * 菜单表 (menu) - 仓储服务接口
 *
 * @author Baolong 2025年03月21 22:38
 * @version 1.0
 * @since 1.8
 */
public interface MenuRepository {

	/**
	 * 添加菜单
	 *
	 * @param menu 菜单领域对象
	 * @return 菜单ID
	 */
	Long addMenu(Menu menu);

	/**
	 * 删除菜单
	 *
	 * @param menuId 菜单ID
	 * @return 是否成功
	 */
	boolean deleteMenu(Long menuId);

	/**
	 * 更新菜单
	 *
	 * @param menu 菜单领域对象
	 */
	boolean updateMenu(Menu menu);

	/**
	 * 根据菜单路径判断菜单是否存在
	 *
	 * @param menuPath 菜单路径
	 * @return true:存在 false:不存在
	 */
	boolean existedMenuByMenuPath(String menuPath);

	/**
	 * 根据菜单ID判断菜单是否存在
	 *
	 * @param menuId 菜单ID
	 * @return true:存在 false:不存在
	 */
	boolean existedMenuByMenuId(Long menuId);

	/**
	 * 根据菜单ID列表获取菜单列表
	 *
	 * @param menuIdList 菜单ID列表
	 * @return 菜单列表
	 */
	List<Menu> getMenuListByMenuIdList(Set<Long> menuIdList);

	/**
	 * 根据菜单路径获取菜单
	 *
	 * @param menuPath 菜单路径
	 * @return 菜单
	 */
	Menu getMenuByMenuPath(String menuPath);

	/**
	 * 获取菜单管理分页列表
	 *
	 * @param menu 菜单领域对象
	 * @return 菜单管理分页列表
	 */
	PageVO<Menu> getMenuPageList(Menu menu);
}
