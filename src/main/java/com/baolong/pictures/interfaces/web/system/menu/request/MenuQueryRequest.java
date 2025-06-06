package com.baolong.pictures.interfaces.web.system.menu.request;

import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MenuQueryRequest extends PageRequest implements Serializable {

	/**
 * 菜单ID
 */
	private Long menuId;

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

	/**
	 * 是否禁用（0-正常, 1-禁用）
	 */
	private Integer isDisabled;

	/**
	 * 编辑时间
	 */
	private Date editTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	private static final long serialVersionUID = 1L;
}
