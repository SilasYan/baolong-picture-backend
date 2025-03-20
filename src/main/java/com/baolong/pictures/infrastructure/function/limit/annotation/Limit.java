package com.baolong.pictures.infrastructure.function.limit.annotation;

import com.baolong.pictures.infrastructure.function.limit.enums.LimitType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求频率限流注解
 *
 * @author Baolong 2025年03月08 02:17
 * @version 1.0
 * @since 1.8
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Limit {
	/**
	 * 限流 KEY
	 */
	String key() default "LIMIT:";

	/**
	 * 限流时间，默认10秒，单位秒
	 */
	int time() default 10;

	/**
	 * 允许请求的次数，默认5次
	 */
	int count() default 5;

	/**
	 * 限制类型
	 */
	LimitType limitType() default LimitType.DEFAULT;

	String errMsg() default "请求过于频繁，请稍后再试";
}
