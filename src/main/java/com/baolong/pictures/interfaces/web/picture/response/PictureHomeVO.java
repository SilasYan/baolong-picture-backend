package com.baolong.pictures.interfaces.web.picture.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片首页响应对象
 */
@Data
public class PictureHomeVO implements Serializable {

	/**
	 * 图片 ID
	 */
	private Long pictureId;

	/**
	 * 图片名称（展示）
	 */
	private String picName;

	/**
	 * 图片描述（展示）
	 */
	private String picDesc;

	/**
	 * 图片地址（展示, 压缩图地址）
	 */
	private String picUrl;

	/**
	 * 缩略图地址
	 */
	private String thumbnailUrl;

	/**
	 * 分类 ID
	 */
	private Long categoryId;

	/**
	 * 标签（逗号分隔的标签列表）
	 */
	private String tags;

	/**
	 * 查看数量
	 */
	private Integer viewQuantity;

	/**
	 * 点赞数量
	 */
	private Integer likeQuantity;

	/**
	 * 收藏数量
	 */
	private Integer collectQuantity;

	/**
	 * 下载数量
	 */
	private Integer downloadQuantity;

	/**
	 * 分享数量
	 */
	private Integer shareQuantity;

	/**
	 * 创建用户 ID
	 */
	private Long userId;

	/**
	 * 创建用户昵称
	 */
	private String userName;

	/**
	 * 创建用户头像
	 */
	private String userAvatar;

	/**
	 * 是否分享（0-分享, 1-不分享）
	 */
	private Integer isShare;

	/**
	 * 登录用户是否点赞
	 */
	private Boolean loginUserIsLike = false;

	/**
	 * 登录用户是否收藏
	 */
	private Boolean loginUserIsCollect = false;

	private static final long serialVersionUID = 1L;
}
