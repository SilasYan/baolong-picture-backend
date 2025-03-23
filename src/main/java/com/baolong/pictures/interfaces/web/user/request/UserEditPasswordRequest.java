package com.baolong.pictures.interfaces.web.user.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户编辑请求
 */
@Data
public class UserEditPasswordRequest implements Serializable {

	/**
	 * 原密码
	 */
	private String originPassword;
	/**
	 * 新密码
	 */
	private String newPassword;
	/**
	 * 确认密码
	 */
	private String confirmPassword;

	private static final long serialVersionUID = 1L;
}
