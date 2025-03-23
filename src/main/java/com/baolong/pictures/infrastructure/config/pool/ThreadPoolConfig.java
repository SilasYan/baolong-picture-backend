package com.baolong.pictures.infrastructure.config.pool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置类
 *
 * @author Baolong 2025年03月19 11:11
 * @version 1.0
 * @since 1.8
 */
@Configuration
// @EnableAsync // 开启异步任务, 启动类已开启
public class ThreadPoolConfig {
	/**
	 * 邮件发送线程池
	 *
	 * @return 线程池任务执行器
	 */
	@Bean(name = "emailThreadPool")
	public ThreadPoolTaskExecutor emailThreadPool() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 设置核心线程数,它是可以同时被执行的线程数量
		executor.setCorePoolSize(2);
		// 设置最大线程数,缓冲队列满了之后会申请超过核心线程数的线程
		executor.setMaxPoolSize(10);
		// 设置缓冲队列容量,在执行任务之前用于保存任务
		executor.setQueueCapacity(50);
		// 设置线程生存时间（秒）,当超过了核心线程出之外的线程在生存时间到达之后会被销毁
		executor.setKeepAliveSeconds(60);
		// 设置线程名称前缀
		executor.setThreadNamePrefix("emailPool-");
		// 设置拒绝策略
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 等待所有任务结束后再关闭线程池
		executor.setWaitForTasksToCompleteOnShutdown(true);
		// 初始化
		executor.initialize();
		return executor;
	}

	/**
	 * 消息发送线程池
	 *
	 * @return 线程池任务执行器
	 */
	@Bean(name = "messageThreadPool")
	public ThreadPoolTaskExecutor messageThreadPool() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		// 设置核心线程数,它是可以同时被执行的线程数量
		executor.setCorePoolSize(2);
		// 设置最大线程数,缓冲队列满了之后会申请超过核心线程数的线程
		executor.setMaxPoolSize(10);
		// 设置缓冲队列容量,在执行任务之前用于保存任务
		executor.setQueueCapacity(50);
		// 设置线程生存时间（秒）,当超过了核心线程出之外的线程在生存时间到达之后会被销毁
		executor.setKeepAliveSeconds(60);
		// 设置线程名称前缀
		executor.setThreadNamePrefix("messagePool-");
		// 设置拒绝策略
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		// 等待所有任务结束后再关闭线程池
		executor.setWaitForTasksToCompleteOnShutdown(true);
		// 初始化
		executor.initialize();
		return executor;
	}
}
