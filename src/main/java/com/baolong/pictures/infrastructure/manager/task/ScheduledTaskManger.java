package com.baolong.pictures.infrastructure.manager.task;

import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.persistence.system.task.mybatis.ScheduledTaskDO;
import com.baolong.pictures.infrastructure.persistence.system.task.mybatis.ScheduledTaskPersistenceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 定时任务管理器
 * <p>
 * 核心功能：
 * <p>
 * 1. 系统启动时初始化定时任务
 * <p>
 * 2. 提供任务动态刷新能力
 * <p>
 * 3. 维护任务生命周期
 *
 * @author Baolong 2025年03月19 14:09
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@AllArgsConstructor
@SpringBootConfiguration
public class ScheduledTaskManger {

	/**
	 * 任务开启状态
	 */
	private static final Integer OPEN = 1;
	/**
	 * 任务关闭状态
	 */
	private static final Integer CLOSE = 0;

	// region 依赖注入组件

	/**
	 * 获取 Spring 容器中所有的 Task 任务类中的 bean
	 */
	private final Map<String, Task> allWorkerMap;
	/**
	 * 定时任务线程池
	 */
	private final ThreadPoolTaskScheduler threadPoolTaskScheduler;
	/**
	 * 定时任务注册表
	 */
	private final Map<String, ScheduledFuture<?>> scheduledFutureRegistry;
	/**
	 * 定时任务操作注册表
	 */
	private final Map<Integer, Consumer<ScheduledTaskDO>> operationRegistry;
	/**
	 * 定时任务 Service 对象
	 */
	private final ScheduledTaskPersistenceService scheduledTaskPersistenceService;

	// endregion 依赖注入组件

	/**
	 * 所有任务列表
	 */
	private List<ScheduledTaskDO> taskList;

	/**
	 * 系统初始化入口
	 */
	@PostConstruct
	public void initTask() {
		log.info("开始初始化定时任务...");
		reloadTaskList();
		initScheduledFuture();
		initOperationMap();
		log.info("定时任务初始化完成，共加载{}个任务", scheduledFutureRegistry.keySet().size());
	}

	/**
	 * 重新加载任务列表
	 */
	private void reloadTaskList() {
		taskList = scheduledTaskPersistenceService.list();
	}

	/**
	 * 初始化任务注册表
	 * <p>
	 * 开启所有处于开启状态的任务，并将其 ScheduledFuture 对象注册到注册表中
	 */
	private void initScheduledFuture() {
		// 获取所有开启的任务
		List<ScheduledTaskDO> openJobs = taskList.stream()
				.filter(job -> job.getTaskStatus().equals(OPEN))
				.collect(Collectors.toList());
		// 开启这些任务并注册到注册表中
		openJobs.forEach(job -> {
			Task task = allWorkerMap.get(job.getTaskBean());
			if (task != null) {
				ScheduledFuture<?> schedule = threadPoolTaskScheduler.schedule(task, this.toCronTrigger(job.getTaskCron()));
				// 把当前任务保存到注册表中
				scheduledFutureRegistry.put(job.getTaskKey(), schedule);
			}
			log.error("任务: [{}]未找到对应的目标方法", job.getTaskBean());
		});
	}

	/**
	 * 初始化操作注册表
	 * <p>
	 * 定义任务开启和关闭的操作逻辑，并存储到操作注册表中
	 */
	private void initOperationMap() {
		// 开启操作
		Consumer<ScheduledTaskDO> open = job -> {
			// 将注册表中的任务进行开启操作
			scheduledFutureRegistry.compute(job.getTaskKey(), (k, v) -> {
				// Optional.ofNullable(v).ifPresent(o -> o.cancel(true));
				Task task = allWorkerMap.get(job.getTaskBean());
				if (task == null) {
					throw new BusinessException(ErrorCode.SYSTEM_ERROR, "任务: [" + job.getTaskBean() + "]未找到对应的目标方法");
				}
				// 动态开启定时任务
				return threadPoolTaskScheduler.schedule(task, this.toCronTrigger(job.getTaskCron()));
			});
		};
		operationRegistry.put(OPEN, open);

		// 关闭操作
		Consumer<ScheduledTaskDO> close = cornJob -> {
			// 取消此定时任务
			ScheduledFuture<?> scheduledFuture = scheduledFutureRegistry.get(cornJob.getTaskKey());
			Optional.ofNullable(scheduledFuture).ifPresent(o -> o.cancel(true));
			// 从注册表中删除
			scheduledFutureRegistry.remove(cornJob.getTaskKey());
		};
		operationRegistry.put(CLOSE, close);
	}

	/**
	 * 刷新指定任务
	 *
	 * @param taskId 任务 ID
	 */
	public void refresh(Long taskId) {
		refresh(scheduledTaskPersistenceService.getById(taskId));
	}

	/**
	 * 刷新所有任务
	 */
	public void refresh() {
		reloadTaskList();
		taskList.forEach(this::refresh);
	}

	/**
	 * 刷新指定任务
	 *
	 * @param task 任务对象
	 */
	public void refresh(ScheduledTaskDO task) {
		operationRegistry.get(task.getTaskStatus()).accept(task);
	}

	/**
	 * 将 cron 表达式转为 CronTrigger
	 *
	 * @param cronExpressions cron 表达式
	 * @return CronTrigger
	 */
	public CronTrigger toCronTrigger(String cronExpressions) {
		return new CronTrigger(cronExpressions);
	}
}
