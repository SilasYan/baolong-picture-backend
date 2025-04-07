package com.baolong.pictures.interfaces.web.category.request;

import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 分类标签查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CategoryQueryRequest extends PageRequest implements Serializable {

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

	private static final long serialVersionUID = 1L;
}
