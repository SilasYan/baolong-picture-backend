package com.baolong.pictures.application.shared.auth.aop;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.domain.user.aggregate.enums.UserRoleEnum;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.manager.redis.RedisCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 权限校验拦截器
 */
@Aspect
@Component
public class AuthCheckInterceptor {

	@Resource
	private RedisCache redisCache;

	/**
	 * 执行拦截
	 *
	 * @param joinPoint 切入点
	 * @param authCheck 权限校验注解
	 */
	@Around("@annotation(authCheck)")
	public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
		// 需要登录，校验登录
		if (authCheck.isLogin()) {
			if (!StpUtil.isLogin()) {
				throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "请先登录");
			}
		}
		// 校验是否指定角色
		String mustRole = authCheck.mustRole();
		UserRoleEnum roleEnum = UserRoleEnum.of(mustRole);
		// 没有指定角色，直接放行
		if (roleEnum == null) {
			return joinPoint.proceed();
		}
		// 获取当前登录用户
		String value = redisCache.get(UserConstant.USER_LOGIN_STATE + StpUtil.getLoginIdAsLong());
		ThrowUtils.throwIf(StrUtil.isEmpty(value), ErrorCode.NOT_LOGIN_ERROR, "请先登录");
		User loginUser = JSONUtil.toBean(value, User.class);
		// 校验当前用户的权限
		ThrowUtils.throwIf(!mustRole.equals(loginUser.getUserRole()), ErrorCode.NO_AUTH_ERROR, "没有权限");
		// 通过, 放行
		return joinPoint.proceed();
	}
}
