package com.baolong.pictures.interfaces.controller;

import com.baolong.pictures.application.service.MainApplicationService;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.interfaces.vo.CaptchaVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/")
public class MainController {

	@Resource
	private MainApplicationService mainApplicationService;

	/**
	 * 健康检查
	 */
	@GetMapping("/health")
	public BaseResponse<String> health() {
		return ResultUtils.success("ok");
	}

	/**
	 * 获取图形验证码
	 */
	@GetMapping("/captcha")
	public BaseResponse<CaptchaVO> captcha() {
		return ResultUtils.success(mainApplicationService.getCaptcha());
	}
}
