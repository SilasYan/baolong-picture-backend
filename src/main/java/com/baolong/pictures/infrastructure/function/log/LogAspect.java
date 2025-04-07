package com.baolong.pictures.infrastructure.function.log;

import cn.hutool.json.JSONUtil;
import com.baolong.pictures.infrastructure.persistence.system.log.entity.RequestLog;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.function.log.annotation.Log;
import com.baolong.pictures.infrastructure.utils.ServletUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 请求日志切面
 *
 * @author Baolong 2025年03月08 01:50
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Aspect
@Component
public class LogAspect {
	private final ThreadLocal<RequestLog> threadLocal = new ThreadLocal<>();

	/**
	 * 定义切入点
	 */
	@Pointcut("@annotation(com.baolong.pictures.infrastructure.function.log.annotation.Log)")
	public void pointCut() {
	}

	/**
	 * 环绕通知,执行Controller方法的前后执行
	 *
	 * @param joinPoint 连接点
	 */
	@Around("pointCut()")
	public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		// 获取当前请求对象
		HttpServletRequest request = ServletUtils.getRequest();
		// 获取请求相关信息
		RequestLog entity = new RequestLog();
		entity.setReqPath(request.getRequestURI());
		entity.setReqMethod(request.getMethod());
		entity.setReqIp(request.getRemoteHost());
		entity.setReqTime(new Date());

		// 反射获取调用方法
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		if (method.isAnnotationPresent(Log.class)) {
			// 获取注解信息
			Log annotation = method.getAnnotation(Log.class);
			entity.setOpName(annotation.opName());
			entity.setOpDesc(annotation.opDesc());
		}
		// 获取全限定类名称
		String name = method.getName();
		Class<?> targetClass = method.getDeclaringClass();
		entity.setQualifiedName(targetClass + "[" + name + "]");

		// 获取请求参数
		entity.setInputParam(JSONUtil.toJsonStr(joinPoint.getArgs()));

		// TODO 需要实现的
		// entity.setUserId();
		// entity.setSource();
		// entity.setDeviceType();
		// entity.setOsType();
		// entity.setOsVersion();
		// entity.setBrowserName();
		// entity.setBrowserVersion();

		// 设置局部变量
		threadLocal.set(entity);

		// 调用Controller方法
		return joinPoint.proceed();
	}

	/**
	 * 返回值通知,Controller执行完成之后,返回方法的返回值时候执行
	 *
	 * @param ret 返回值的名称
	 */
	@AfterReturning(pointcut = "pointCut()", returning = "ret")
	public Object afterReturning(Object ret) {
		// 获取日志实体对象
		RequestLog entity = threadLocal.get();
		// 保存响应参数
		entity.setOutputParam(JSONUtil.toJsonStr(ret));
		// 设置成功标识
		entity.setReqStatus(ErrorCode.SUCCESS.getCode());
		entity.setLogLevel("INFO");

		// 一定要删除 ThreadLocal 变量
		threadLocal.remove();

		log.info("请求成功: {}", JSONUtil.parse(entity));
		// TODO 持久化到数据库日志表
		return ret;
	}

	/**
	 * 异常通知,当Controller方法执行过程中出现异常时候,执行该通知
	 *
	 * @param ex 异常名称
	 */
	@AfterThrowing(pointcut = "pointCut()", throwing = "ex")
	public void throwingAdvice(Throwable ex) {
		// 获取日志实体对象
		RequestLog entity = this.getEntity();
		// 保存错误信息
		entity.setErrorMsg(ex.toString());

		// 设置失败标识
		entity.setReqStatus(ErrorCode.FAILED.getCode());
		entity.setLogLevel("ERROR");

		// 一定要删除 ThreadLocal 变量
		threadLocal.remove();

		log.error("请求失败: {}", JSONUtil.parse(entity));

		// TODO 持久化到数据库日志表
	}

	private RequestLog getEntity() {
		// 获取局部变量
		RequestLog entity = threadLocal.get();
		long start = entity.getReqTime().getTime();
		Date end = new Date();
		// long end = System.currentTimeMillis();
		// 获取响应时间、耗时
		entity.setCostTime(end.getTime() - start);
		entity.setRespTime(end);
		return entity;
	}

}
