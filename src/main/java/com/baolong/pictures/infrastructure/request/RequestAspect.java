package com.baolong.pictures.infrastructure.request;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 请求切面,
 * <p>
 * 打印入参等信息
 *
 * @author Baolong 2025年03月16 16:29
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Aspect
@Component
@Order(1)
public class RequestAspect {
	@Around("@within(org.springframework.web.bind.annotation.RestController)" +
			"||@within(org.springframework.stereotype.Controller)")
	public Object after(ProceedingJoinPoint joinPoint) throws Throwable {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		log.info("↓↓↓↓↓↓↓↓↓↓ 请求日志 ↓↓↓↓↓↓↓↓↓↓");
		log.info("请求接口: [{}] {}", request.getMethod(), request.getRequestURI());
		log.info("请求方法: {}.{}", joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName());
		log.info("请求参数: {}", JSONUtil.toJsonStr(filterArgs(joinPoint.getArgs())));
		long start = System.currentTimeMillis();
		Object result = joinPoint.proceed(joinPoint.getArgs());
		long end = System.currentTimeMillis();
		log.info("接口耗时: {} ms", end - start);
		log.info("↑↑↑↑↑↑↑↑↑↑ 请求日志 ↑↑↑↑↑↑↑↑↑↑");
		return result;
	}

	private List<Object> filterArgs(Object[] objects) {
		return Arrays.stream(objects).filter(obj -> !(obj instanceof MultipartFile)
				&& !(obj instanceof HttpServletResponse)
				&& !(obj instanceof HttpServletRequest)).collect(Collectors.toList());
	}
}
