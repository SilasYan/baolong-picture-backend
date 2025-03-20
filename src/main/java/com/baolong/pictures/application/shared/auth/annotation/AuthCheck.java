package com.baolong.pictures.application.shared.auth.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限校验注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

	/**
	 * 是否需要登录
	 */
	boolean isLogin() default true;

	/**
	 * 必须的角色
	 *
	 * @return 角色字符串
	 */
	String mustRole() default "";
}
