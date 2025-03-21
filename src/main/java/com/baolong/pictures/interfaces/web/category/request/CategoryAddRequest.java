package com.baolong.pictures.interfaces.web.category.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类新增请求
 */
@Data
public class CategoryAddRequest implements Serializable {

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 父分类 ID（0-表示顶层分类）
	 */
	private Long parentId;

	private static final long serialVersionUID = 1L;
}
