package com.baolong.pictures.infrastructure.common.constant;

/**
 * 缓存 KEY 常量接口
 *
 * @author Baolong 2025年03月06 22:58
 * @version 1.0
 * @since 1.8
 */
public interface CacheKeyConstant {
	/**
	 * 邮箱验证码缓存 KEY 前缀
	 */
	String EMAIL_CODE_KEY = "EMAIL_CODE_KEY:%s:%s";

	/**
	 * 图形验证码缓存 KEY 前缀
	 */
	String CAPTCHA_CODE_KEY = "CAPTCHA_CODE_KEY:%s";

	/**
	 * 图片列表缓存 KEY
	 */
	String PICTURE_LIST_KEY = "PICTURE_LIST_KEY:%s";
}
