package com.baolong.pictures.infrastructure.function.limit;

import cn.hutool.extra.servlet.ServletUtil;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.function.limit.annotation.Limit;
import com.baolong.pictures.infrastructure.function.limit.enums.LimitType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

/**
 * 请求频率限流切面
 *
 * @author Baolong 2025年03月08 02:18
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Aspect
@Component
public class LimitAspect {
	@Resource
	private RedisTemplate<Object, Object> redisTemplate;
	@Resource
	private RedisScript<Long> limitScript;

	@Before("@annotation(limit)")
	public void doBefore(JoinPoint point, Limit limit) {
		String key = limit.key();
		int time = limit.time();
		int count = limit.count();
		String combineKey = getCombineKey(limit, point);
		List<Object> keys = Collections.singletonList(combineKey);
		Long number = redisTemplate.execute(limitScript, keys, count, time);
		if (number == null || number.intValue() > count) {
			throw new BusinessException(ErrorCode.REQUEST_LIMIT_ERROR, limit.errMsg());
		}
		log.info("[限流]当前 {}/{} 次请求, 限流 KEY: [{}]", number.intValue(), count, combineKey);
	}

	public String getCombineKey(Limit limit, JoinPoint point) {
		StringBuilder sb = new StringBuilder(limit.key());
		MethodSignature signature = (MethodSignature) point.getSignature();
		Method method = signature.getMethod();
		Class<?> targetClass = method.getDeclaringClass();
		if (limit.limitType() == LimitType.GLOBAL) {
			sb.append(targetClass.getName()).append(":").append(method.getName());
		}
		if (limit.limitType() == LimitType.CLASS_METHOD_IP || limit.limitType() == LimitType.CLASS_METHOD_UID) {
			sb.append(targetClass.getName()).append(":").append(method.getName()).append(":");
		}
		if (limit.limitType() == LimitType.DEFAULT || limit.limitType() == LimitType.IP
				|| limit.limitType() == LimitType.CLASS_METHOD_IP
				|| limit.limitType() == LimitType.CLASS_METHOD_UID) {
			sb.append(ServletUtil.getClientIP(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest()));
		}
		if (limit.limitType() == LimitType.UID) {
			// TODO 等待实现，以用户 UID 做限流
		}
		return sb.toString();
	}
}
