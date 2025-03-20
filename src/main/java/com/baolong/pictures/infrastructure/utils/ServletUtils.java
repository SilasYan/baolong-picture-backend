package com.baolong.pictures.infrastructure.utils;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet 工具类
 * <p>
 * 用于获取Request、Response相关信息
 *
 * @author Baolong 2025年03月07 20:16
 * @version 1.0
 * @since 1.8
 */
public class ServletUtils {
	/**
	 * 从 SpringBoot 中获取 Request 请求对象
	 *
	 * @return 返回当前请求的 Request 对象
	 */
	public static HttpServletRequest getRequest() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			throw new RuntimeException("当前线程中不存在 Request 请求");
		}
		ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
		return attributes.getRequest();
	}

	/**
	 * 从 SpringBoot 中获取 Response 请求对象
	 *
	 * @return 返回当前请求的 Response 对象
	 */
	public static HttpServletResponse getResponse() {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes == null) {
			throw new RuntimeException("当前线程中不存在 Request 请求");
		}
		ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
		return attributes.getResponse();
	}
}
