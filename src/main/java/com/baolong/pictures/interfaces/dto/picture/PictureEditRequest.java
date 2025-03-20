package com.baolong.pictures.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片修改请求
 */
@Data
public class PictureEditRequest implements Serializable {

	/**
	 * 图片 ID
	 */
	private Long id;

	/**
	 * 图片名称（展示）
	 */
	private String picName;

	/**
	 * 图片描述（展示）
	 */
	private String picDesc;

	/**
	 * 分类 ID
	 */
	private Long categoryId;

	/**
	 * 标签列表
	 */
	private List<String> tags;

	/**
	 * 空间 ID
	 */
	private Long spaceId;

	private static final long serialVersionUID = 1L;
}
