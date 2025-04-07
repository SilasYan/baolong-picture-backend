package com.baolong.pictures.domain.system.menu.aggregate;

import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 菜单领域模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Menu extends PageRequest implements Serializable {

	// region 原始属性

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

	// endregion 原始属性

	// region 领域方法

	/**
	 * 校验参数
	 */
	public void  checkParam() {
		ThrowUtils.throwIf(StrUtil.isEmpty(this.menuName), ErrorCode.PARAMS_ERROR, "菜单名称不能为空");
		ThrowUtils.throwIf(this.menuPosition == null, ErrorCode.PARAMS_ERROR, "菜单位置不能为空");
		ThrowUtils.throwIf(StrUtil.isEmpty(this.menuPath), ErrorCode.PARAMS_ERROR, "路由路径不能为空");
	}

	// endregion 领域方法

	private static final long serialVersionUID = 1L;
}
