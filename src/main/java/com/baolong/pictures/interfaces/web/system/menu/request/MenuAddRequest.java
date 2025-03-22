package com.baolong.pictures.interfaces.web.system.menu.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 菜单新增请求
 */
@Data
public class MenuAddRequest implements Serializable {

	/**
	 * 菜单名称
	 */
	private String menuName;

	/**
	 * 菜单位置（1-顶部, 2-左侧）
	 */
	private Integer menuPosition;

	/**
	 * 路由路径（vue-router path）
	 */
	private String menuPath;

	/**
	 * 父菜单 ID
	 */
	private Long parentId;

	private static final long serialVersionUID = 1L;
}
