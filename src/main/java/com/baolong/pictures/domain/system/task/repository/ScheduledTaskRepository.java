package com.baolong.pictures.domain.system.task.repository;

import com.baolong.pictures.domain.system.task.aggregate.ScheduledTask;
import com.baolong.pictures.infrastructure.common.page.PageVO;

/**
 * 定时任务表 (scheduled_task) - 仓储服务接口
 *
 * @author Silas Yan 2025-03-22:15:51
 */
public interface ScheduledTaskRepository {

	/**
	 * 新增定时任务
	 *
	 * @param scheduledTask 定时任务领域对象
	 * @return 是否成功
	 */
	boolean addScheduledTask(ScheduledTask scheduledTask);

	/**
	 * 删除定时任务
	 *
	 * @param taskId 任务ID
	 * @return 是否成功
	 */
	boolean deleteScheduledTask(Long taskId);

	/**
	 * 更新定时任务
	 *
	 * @param scheduledTask 定时任务领域对象
	 */
	boolean updateScheduledTask(ScheduledTask scheduledTask);

	/**
	 * 根据任务ID判断任务是否存在
	 *
	 * @param taskId 任务ID
	 * @return 是否存在
	 */
	boolean existedScheduledTaskByTaskId(Long taskId);

	/**
	 * 根据任务KEY或任务方法判断任务是否存在
	 *
	 * @param taskBean 任务方法/任务KEY
	 * @return 是否存在
	 */
	boolean existedScheduledTaskByTaskKeyOrTaskBean(String taskBean);

	/**
	 * 获取定时任务管理分页列表
	 *
	 * @param scheduledTask 定时任务领域对象
	 * @return 定时任务管理分页列表
	 */
	PageVO<ScheduledTask> getScheduledTaskPage(ScheduledTask scheduledTask);

	/**
	 * 根据任务方法获取定时任务
	 *
	 * @param taskBean 任务方法
	 * @return 定时任务
	 */
	ScheduledTask getScheduledTaskByTaskBean(String taskBean);
}
