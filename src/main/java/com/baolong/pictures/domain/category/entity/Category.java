package com.baolong.pictures.domain.category.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 分类表
 *
 * @TableName category
 */
@TableName(value = "category")
@Data
public class Category implements Serializable {

	// region 属性

	/**
	 * 主键 ID
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 父分类 ID（0-表示顶层分类）
	 */
	private Long parentId;

	/**
	 * 使用数量
	 */
	private Integer useNum;

	/**
	 * 创建用户 ID
	 */
	private Long userId;

	/**
	 * 是否删除
	 */
	private Integer isDelete;

	/**
	 * 编辑时间
	 */
	@TableField(value = "edit_time", fill = FieldFill.UPDATE)
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

	// endregion 属性

	// region 行为

	/**
	 * 填充编辑时间
	 */
	public void fillEditTime() {
		this.editTime = new Date();
	}

	// endregion 行为

}
