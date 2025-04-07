package com.baolong.pictures.interfaces.web.system.menu.assembler;

import com.baolong.pictures.domain.system.menu.aggregate.Menu;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.system.menu.request.MenuAddRequest;
import com.baolong.pictures.interfaces.web.system.menu.request.MenuQueryRequest;
import com.baolong.pictures.interfaces.web.system.menu.request.MenuUpdateRequest;
import com.baolong.pictures.interfaces.web.system.menu.response.MenuVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 菜单转换类
 *
 * @author Baolong 2025年03月04 23:37
 * @version 1.0
 * @since 1.8
 */
public class MenuAssembler {

	/**
	 * 菜单更新请求 转为 菜单领域对象
	 */
	public static Menu toDomain(MenuAddRequest menuAddRequest) {
		Menu menu = new Menu();
		BeanUtils.copyProperties(menuAddRequest, menu);
		return menu;
	}

	/**
	 * 菜单更新请求 转为 菜单领域对象
	 */
	public static Menu toDomain(MenuUpdateRequest menuUpdateRequest) {
		Menu menu = new Menu();
		BeanUtils.copyProperties(menuUpdateRequest, menu);
		return menu;
	}

	/**
	 * 菜单查询请求 转为 菜单领域对象
	 */
	public static Menu toDomain(MenuQueryRequest menuQueryRequest) {
		Menu menu = new Menu();
		BeanUtils.copyProperties(menuQueryRequest, menu);
		return menu;
	}

	/**
	 * 菜单领域对象 转为 菜单响应对象
	 */
	public static MenuVO toVO(Menu menu) {
		MenuVO menuVO = new MenuVO();
		BeanUtils.copyProperties(menu, menuVO);
		return menuVO;
	}

	/**
	 * 菜单领域对象分页 转为 菜单响应对象分页
	 */
	public static PageVO<MenuVO> toPageVO(PageVO<Menu> menuPageVO) {
		return new PageVO<>(menuPageVO.getCurrent()
				, menuPageVO.getPageSize()
				, menuPageVO.getTotal()
				, menuPageVO.getPages()
				, Optional.ofNullable(menuPageVO.getRecords())
				.orElse(List.of()).stream()
				.map(MenuAssembler::toVO)
				.collect(Collectors.toList())
		);
	}
}
