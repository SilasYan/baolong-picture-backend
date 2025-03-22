package com.baolong.pictures.domain.menu.repository;

import com.baolong.pictures.domain.menu.entity.Menu;

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
	 * 根据菜单ID列表获取菜单列表
	 *
	 * @param menuIdList 菜单ID列表
	 * @return 菜单列表
	 */
	List<Menu> getMenuListByMenuIdList(Set<Long> menuIdList);
}
