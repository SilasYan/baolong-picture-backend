package com.baolong.pictures.infrastructure.manager.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 定时任务类
 * <p>
 * 通过@Bean声明具体的定时任务实例。演示两种任务定义方式：
 * <p>
 * 1. 使用Lambda表达式定义任务逻辑（推荐简洁写法）
 * <p>
 * 2. 通过匿名内部类定义（演示传统写法）
 *
 * @author Baolong 2025年03月19 13:55
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Component
public class BaseTask {
	/**
	 * 定义任务A（显式指定Bean名称）
	 *
	 * @return Task实例 - 使用Lambda表达式实现
	 */
	@Bean(name = "task")
	public Task taskA() {
		return () -> System.out.println(Thread.currentThread().getName() + "执行任务A......");
	}

	/**
	 * 定义任务B（默认使用方法名作为Bean名称）
	 *
	 * @return Task实例 - 使用匿名内部类实现
	 */
	@Bean
	public Task taskB() {
		return new Task() {
			@Override
			public void doTask() {
				System.out.println(Thread.currentThread().getName() + "执行任务B......");
			}
		};
	}
}
