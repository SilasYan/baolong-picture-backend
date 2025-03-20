package com.baolong.pictures.application.service;

import com.baolong.pictures.interfaces.web.response.CaptchaVO;

/**
 * 公共应用服务接口
 *
 * @author Baolong 2025年03月06 23:13
 * @version 1.0
 * @since 1.8
 */
public interface MainApplicationService {
	/**
	 * 获取图形验证码
	 *
	 * @return 图形验证码VO
	 */
	CaptchaVO getCaptcha();
}
