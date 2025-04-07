package com.baolong.pictures.interfaces.web.picture.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片上传请求
 */
@Data
public class PictureUploadRequest implements Serializable {

	/**
	 * 图片 ID
	 */
	private Long pictureId;

	/**
	 * 图片地址
	 */
	private String pictureUrl;

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

	/**
	 * 扩图状态（0-普通图片, 1-扩图图片, 2-扩图成功后的图片）
	 */
	private Integer expandStatus;

	private static final long serialVersionUID = 1L;
}
