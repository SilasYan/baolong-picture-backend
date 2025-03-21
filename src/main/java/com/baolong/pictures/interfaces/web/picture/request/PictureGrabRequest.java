package com.baolong.pictures.interfaces.web.picture.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片抓取请求
 */
@Data
public class PictureGrabRequest implements Serializable {

	/**
	 * 爬取来源
	 */
	private String grabSource;

	/**
	 * 关键词
	 */
	private String keyword;

	/**
	 * 名称前缀
	 */
	private String namePrefix;

	/**
	 * 爬取数量
	 */
	private Integer grabCount = 15;

	/**
	 * 随机种子, 应该大于 0 小于 100
	 */
	private Integer randomSeed;

	private static final long serialVersionUID = 1L;
}
