package com.baolong.pictures.interfaces.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.servlet.ServletUtil;
import com.baolong.pictures.application.service.VipApplicationService;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.function.limit.annotation.Limit;
import com.baolong.pictures.infrastructure.function.log.annotation.Log;
import com.baolong.pictures.infrastructure.manager.message.EmailManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 会员接口
 *
 * @author Baolong 2025年03月08 01:29
 * @version 1.0
 * @since 1.8
 */
@RestController
@RequestMapping("/vip")
public class VipController {

	@Resource
	private VipApplicationService vipApplicationService;
	@Resource
	private EmailManager emailManager;

	@Limit
	@Log(opName = "获取会员列表", opDesc = "一个描述")
	@GetMapping("/list")
	public BaseResponse<String> test(HttpServletRequest request) {
		StpUtil.login("100001");
		System.out.println(StpUtil.isLogin());
		String code = RandomUtil.randomNumbers(4);
		// Map<String, Object> contentMap = new HashMap<>();
		// contentMap.put("code", code);
		// emailManager.sendEmail("yz15279292310@qq.com", "注册验证码 - 暴龙图库", contentMap);
		System.out.println(ServletUtil.getClientIP(request));
		return ResultUtils.success("vip list");
	}

	@GetMapping("/get")
	public BaseResponse<String> get(HttpServletRequest request) {
		if (StpUtil.isLogin()) {
			return ResultUtils.success("登陆成功");
		} else {
			return ResultUtils.success("未登录");
		}
	}
}
