package com.baolong.pictures.domain.menu.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色菜单关联表
 *
 * @TableName role_menu
 */
@TableName(value = "role_menu")
@Data
public class RoleMenu implements Serializable {

	/**
	 * 角色 KEY
	 */
	private String roleKey;

	/**
	 * 菜单 ID
	 */
	private Long menuId;

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}
