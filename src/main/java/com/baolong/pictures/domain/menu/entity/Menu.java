package com.baolong.pictures.domain.menu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单表
 *
 * @TableName menu
 */
@TableName(value = "menu")
@Data
public class Menu implements Serializable {

	/**
	 * 菜单ID
	 */
	@TableId(type = IdType.AUTO)
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
	 * 是否删除（0-正常, 1-删除）
	 */
	private Integer isDelete;

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

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}
