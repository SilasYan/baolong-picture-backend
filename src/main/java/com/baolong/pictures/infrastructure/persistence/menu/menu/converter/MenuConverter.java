package com.baolong.pictures.infrastructure.persistence.menu.menu.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.baolong.pictures.domain.menu.entity.Menu;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.menu.menu.mybatis.MenuDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模型转化类（领域模型 <=> 持久化模型）
 *
 * @author Baolong 2025年03月20 20:42
 * @version 1.0
 * @since 1.8
 */
public class MenuConverter {

	private final static CopyOptions toDoOption = CopyOptions.create();
	private final static CopyOptions toDomainOption = CopyOptions.create();

	static {
		toDoOption.setFieldMapping(MapUtil.of("menuId", "id"));
		toDomainOption.setFieldMapping(MapUtil.of("id", "menuId"));
	}

	/**
	 * 领域模型 转为 持久化模型
	 *
	 * @param menu 领域模型
	 * @return 持久化模型
	 */
	public static MenuDO toDO(Menu menu) {
		MenuDO menuDO = new MenuDO();
		BeanUtil.copyProperties(menu, menuDO, toDoOption);
		return menuDO;
	}

	/**
	 * 领域模型列表 转为 持久化模型列表
	 */
	public static List<MenuDO> toDOList(List<Menu> menuList) {
		return Optional.ofNullable(menuList)
				.orElse(List.of()).stream()
				.map(MenuConverter::toDO)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型 转为 领域模型
	 *
	 * @param menuDO 持久化模型
	 * @return 领域模型
	 */
	public static Menu toDomain(MenuDO menuDO) {
		Menu menu = new Menu();
		BeanUtil.copyProperties(menuDO, menu, toDomainOption);
		return menu;
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 *
	 * @param menuDOList 持久化模型列表
	 * @return 领域模型列表
	 */
	public static List<Menu> toDomainList(List<MenuDO> menuDOList) {
		return Optional.ofNullable(menuDOList)
				.orElse(List.of())
				.stream().map(MenuConverter::toDomain)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 *
	 * @param menuDOPage 持久化模型分页
	 * @return 领域模型分页
	 */
	public static PageVO<Menu> toDomainPage(Page<MenuDO> menuDOPage) {
		return new PageVO<>(
				menuDOPage.getCurrent()
				, menuDOPage.getSize()
				, menuDOPage.getTotal()
				, menuDOPage.getPages()
				, Optional.ofNullable(menuDOPage.getRecords())
				.orElse(List.of())
				.stream().map(MenuConverter::toDomain)
				.collect(Collectors.toList())
		);
	}
}
