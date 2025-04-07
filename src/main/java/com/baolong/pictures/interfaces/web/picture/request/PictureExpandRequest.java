package com.baolong.pictures.interfaces.web.picture.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片扩图请求
 */
@Data
public class PictureExpandRequest implements Serializable {

	/**
	 * 图片地址
	 */
	private String picUrl;

	/**
	 * 扩图类型（旋转、等比）
	 */
	private Integer expandType;

	private static final long serialVersionUID = 1L;
}
