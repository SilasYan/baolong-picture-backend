package com.baolong.pictures.interfaces.web.picture.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片互动请求
 * <p>
 * 下载、分享、收藏、点赞、浏览
 */
@Data
public class PictureInteractionRequest implements Serializable {

	/**
	 * 图片 ID
	 */
	private Long pictureId;

	/**
	 * 交互类型
	 */
	private Integer interactionType;

	/**
	 * 交互状态（0-存在, 1-取消）
	 */
	private Integer interactionStatus;

	private static final long serialVersionUID = 1L;
}
