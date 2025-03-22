package com.baolong.pictures.infrastructure.persistence.menu.menu.repository;

import com.baolong.pictures.domain.menu.entity.Menu;
import com.baolong.pictures.domain.menu.repository.MenuRepository;
import com.baolong.pictures.infrastructure.persistence.menu.menu.converter.MenuConverter;
import com.baolong.pictures.infrastructure.persistence.menu.menu.mybatis.MenuDO;
import com.baolong.pictures.infrastructure.persistence.menu.menu.mybatis.MenuPersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 菜单表 (menu) - 仓储服务实现
 *
 * @author Baolong 2025年03月21 22:39
 * @version 1.0
 * @since 1.8
 */
@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

	private final MenuPersistenceService menuPersistenceService;

	/**
	 * 根据菜单ID列表获取菜单列表
	 *
	 * @param menuIdList 菜单ID列表
	 * @return 菜单列表
	 */
	@Override
	public List<Menu> getMenuListByMenuIdList(Set<Long> menuIdList) {
		List<MenuDO> menuDOList = menuPersistenceService.listByIds(menuIdList);
		return MenuConverter.toDomainList(menuDOList);
	}
}
