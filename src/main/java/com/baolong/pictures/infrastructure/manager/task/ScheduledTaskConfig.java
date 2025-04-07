package com.baolong.pictures.infrastructure.manager.task;

import com.baolong.pictures.infrastructure.persistence.system.task.mybatis.ScheduledTaskDO;
import lombok.AllArgsConstructor;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

/**
 * 定时任务配置类
 * <p>
 * 配置任务调度所需的线程池、注册表等基础设施组件
 *
 * @author Baolong 2025年03月19 13:56
 * @version 1.0
 * @since 1.8
 */
@AllArgsConstructor
@SpringBootConfiguration
public class ScheduledTaskConfig {

	/**
	 * 配置定时任务线程池
	 * <p>
	 * 配置说明：
	 * <p>
	 * - 核心线程数：设置为 CPU 核心数（根据任务类型可调整）
	 * <p>
	 * - 线程名前缀：方便日志跟踪
	 * <p>
	 * - 优雅停机：等待任务完成最多30秒
	 *
	 * @return 配置好的线程池实例
	 */
	@Bean(name = "threadPoolTaskScheduler")
	public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		threadPoolTaskScheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
		threadPoolTaskScheduler.setThreadNamePrefix("TaskThread: ");
		threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);
		threadPoolTaskScheduler.setAwaitTerminationSeconds(30);
		return threadPoolTaskScheduler;
	}

	/**
	 * 创建任务注册表
	 * <p>
	 * 维护所有定时任务的 ScheduledFuture 引用，用于后续的任务管理（取消、重启等）
	 *
	 * @return 线程安全的注册表实例
	 */
	@Bean(name = "scheduledFutureRegistry")
	public Map<String, ScheduledFuture<?>> scheduledFutureRegistry() {
		return new ConcurrentHashMap<>(16);
	}

	/**
	 * 创建操作注册表
	 * <p>
	 * 预定义任务状态操作映射：
	 * <p>
	 * - OPEN: 启动任务
	 * <p>
	 * - CLOSE: 停止任务
	 *
	 * @return 操作注册表实例
	 */
	@Bean(name = "operationRegistry")
	public Map<Integer, Consumer<ScheduledTaskDO>> operationRegistry() {
		return new ConcurrentHashMap<>(4);
	}
}
