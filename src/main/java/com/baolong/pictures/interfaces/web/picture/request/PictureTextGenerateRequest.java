package com.baolong.pictures.interfaces.web.picture.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片文本生成请求
 */
@Data
public class PictureTextGenerateRequest implements Serializable {

	/**
	 * 图片提示词
	 */
	private String prompt;

	/**
	 * 尺寸
	 */
	private Integer pictureSize;

	private static final long serialVersionUID = 1L;
}
