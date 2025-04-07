package com.baolong.pictures.interfaces.web.picture.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片搜索请求（以图搜图）
 */
@Data
public class PictureSearchRequest implements Serializable {

	/**
	 * 图片ID
	 */
	private Long pictureId;

	/**
	 * 搜索来源
	 */
	private String searchSource;

	/**
	 * 搜索数量
	 */
	private Integer searchCount = 20;

	/**
	 * 随机种子, 应该大于 0 小于 100
	 */
	private Integer randomSeed = 1;

	private static final long serialVersionUID = 1L;
}
