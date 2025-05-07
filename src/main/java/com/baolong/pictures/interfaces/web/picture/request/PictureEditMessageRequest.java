package com.baolong.pictures.interfaces.web.picture.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片编辑消息请求
 *
 * @author Silas Yan 2025-05-06 19:46
 */
@Data
public class PictureEditMessageRequest implements Serializable {

	/**
	 * 编辑消息类型，枚举: PictureEditMsgTypeEnum
	 */
	private String type;

	/**
	 * 编辑动作，枚举: PictureEditActionEnum
	 */
	private String action;

	private static final long serialVersionUID = 1L;
}
