package com.baolong.pictures.interfaces.web.category.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类更新请求
 */
@Data
public class CategoryUpdateRequest implements Serializable {

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

	private static final long serialVersionUID = 1L;
}
