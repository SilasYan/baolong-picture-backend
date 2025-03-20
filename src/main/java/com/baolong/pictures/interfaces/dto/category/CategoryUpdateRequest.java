package com.baolong.pictures.interfaces.dto.category;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类更新请求
 */
@Data
public class CategoryUpdateRequest implements Serializable {

	/**
	 * 分类 ID
	 */
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

	private static final long serialVersionUID = 1L;
}
