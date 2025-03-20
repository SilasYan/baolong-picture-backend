package com.baolong.pictures.interfaces.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片搜索请求
 */
@Data
public class SearchPictureByPictureRequest implements Serializable {

	/**
	 * 图片 id
	 */
	private Long pictureId;

	private static final long serialVersionUID = 1L;
}
