package com.baolong.pictures.interfaces.web.category.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 分类响应对象
 *
 * @author Baolong 2025年03月09 21:13
 * @version 1.0
 * @since 1.8
 */
@Data
public class CategoryVO implements Serializable {

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
	 * 创建时间
	 */
	private Date createTime;

	private static final long serialVersionUID = 1L;
}
