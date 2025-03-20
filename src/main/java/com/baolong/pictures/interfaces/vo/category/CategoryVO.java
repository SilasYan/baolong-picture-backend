package com.baolong.pictures.interfaces.vo.category;

import lombok.Data;

import java.io.Serializable;

/**
 * 分类 VO
 *
 * @author Baolong 2025年03月09 21:13
 * @version 1.0
 * @since 1.8
 */
@Data
public class CategoryVO implements Serializable {

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
