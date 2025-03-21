package com.baolong.pictures.domain.category.aggregate;

import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 分类领域对象
 *
 * @TableName category
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Category extends PageRequest implements Serializable {

	// region 原始属性

	/**
	 * 分类ID
	 */
	private Long categoryId;

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

	private static final long serialVersionUID = 1L;
}
