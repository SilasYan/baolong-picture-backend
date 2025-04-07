package com.baolong.pictures.interfaces.web.user.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {

	/**
	 * 账号
	 */
	private String userAccount;

	/**
	 * 密码
	 */
	private String userPassword;

	/**
	 * 用户邮箱
	 */
	private String userEmail;

	/**
	 * 图形验证码 key
	 */
	private String captchaKey;

	/**
	 * 图形验证码 验证码
	 */
	private String captchaCode;

	private static final long serialVersionUID = 1L;
}
