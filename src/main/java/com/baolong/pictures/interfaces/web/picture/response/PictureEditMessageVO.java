package com.baolong.pictures.interfaces.web.picture.response;

import com.baolong.pictures.interfaces.web.user.response.UserVO;
import lombok.Data;

import java.io.Serializable;

/**
 * 图片编辑消息响应
 *
 * @author Silas Yan 2025-05-06 19:46
 */
@Data
public class PictureEditMessageVO implements Serializable {

	/**
	 * 编辑消息类型，枚举: PictureEditMsgTypeEnum
	 */
	private String type;

	/**
	 * 编辑动作，枚举: PictureEditActionEnum
	 */
	private String action;

	/**
	 * 信息
	 */
	private String message;

	/**
	 * 用户信息
	 */
	private UserVO user;

	/**
	 * 当前编辑的用户
	 */
	private UserVO inUser;

	private static final long serialVersionUID = 1L;
}
