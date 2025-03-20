package com.baolong.pictures.infrastructure.function.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 请求日志注解
 *
 * @author Baolong 2025年03月08 01:48
 * @version 1.0
 * @since 1.8
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Log {
	/**
	 * 操作名称
	 */
	String opName() default "";

	/**
	 * 描述信息
	 */
	String opDesc() default "";
}
