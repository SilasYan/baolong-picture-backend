package com.baolong.pictures.interfaces.web.request.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片颜色搜索请求,
 */
@Data
public class PictureColorSearchRequest implements Serializable {

	/**
	 * 原图主色调
	 */
	private String picColor;

	/**
	 * 用户 ID
	 */
	private Long userId;

	/**
	 * 空间 ID
	 */
	private Long spaceId;

	private static final long serialVersionUID = 1L;
}
