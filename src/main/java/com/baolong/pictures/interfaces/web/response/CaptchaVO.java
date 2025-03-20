package com.baolong.pictures.interfaces.web.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 图形验证码VO
 *
 * @author Baolong 2025年03月07 19:40
 * @version 1.0
 * @since 1.8
 */
@Data
public class CaptchaVO implements Serializable {

	/**
	 * 图形验证码 key
	 */
	private String captchaKey;

	/**
	 * 图形验证码 base64 图片
	 */
	private String captchaImage;

	private static final long serialVersionUID = 1L;
}
