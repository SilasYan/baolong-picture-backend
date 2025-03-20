package com.baolong.pictures.infrastructure.api.grab.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 爬取图片结果
 *
 * @author Baolong 2025年03月15 23:07
 * @version 1.0
 * @since 1.8
 */
@Data
@Accessors(chain = true)
public class GrabPictureResult implements Serializable {

	/**
	 * 图片链接
	 */
	private String imageUrl;

	/**
	 * 处理后的图片链接
	 */
	private String handleImageUrl;

	/**
	 * 图片名称
	 */
	private String imageName;

	private static final long serialVersionUID = 1L;
}
