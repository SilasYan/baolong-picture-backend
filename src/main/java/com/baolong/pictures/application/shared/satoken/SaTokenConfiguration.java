package com.baolong.pictures.application.shared.satoken;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 权限配置
 *
 * @author Baolong 2025年03月10 23:32
 * @version 1.0
 * @since 1.8
 */
@Configuration
public class SaTokenConfiguration implements WebMvcConfigurer {

	// Sa-Token 整合 jwt (Simple 简单模式)
	@Bean
	public StpLogic getStpLogicJwt() {
		return new StpLogicJwtForSimple();
	}

	/**
	 * 注册 Sa-Token 拦截器
	 *
	 * @param registry InterceptorRegistry
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new SaInterceptor(handler -> {
					// 如果是预检请求，则立即返回到前端
					SaRouter.match(SaHttpMethod.OPTIONS)
							.free(r -> System.out.println("--------OPTIONS预检请求，不做处理"))
							.back();
					// SaRouter.match("/**", r -> StpUtil.checkLogin());
					SaRouter
							.match("/**")    // 拦截的 path 列表，可以写多个 */
							// .notMatch("/st/doLogin", "/st/test", "/error", "/icons/icon_zh_48.png")        // 排除掉的 path 列表，可以写多个
							.notMatch("/picture/home/list", "/category/home/list", "/picture/detail")        // 排除掉的 path 列表，可以写多个
							.check(r -> {
								// System.out.println("地址: " + ServletUtils.getRequest().getRequestURI());
								StpUtil.checkLogin();
							});        // 要执行的校验动作，可以写完整的 lambda 表达式
				}))
				.addPathPatterns("/**")
				.excludePathPatterns(
						"/doc.html", "/swagger-resources", "/v2/api-docs", "/error",
						"/captcha", "/user/login", "/user/register", "/user/send/email/code"
				);
	}
}
