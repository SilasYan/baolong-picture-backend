package com.baolong.pictures.application.service.system;

import com.baolong.pictures.domain.system.task.aggregate.ScheduledTask;
import com.baolong.pictures.domain.system.task.service.ScheduledTaskDomainService;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 定时任务表 (scheduled_task) - 应用服务
 *
 * @author Silas Yan 2025-03-22:15:55
 */
@Service
@RequiredArgsConstructor
public class ScheduledTaskApplicationService {

	private final ScheduledTaskDomainService scheduledTaskDomainService;

	/**
	 * 新增定时任务
	 *
	 * @param scheduledTask 定时任务领域对象
	 */
	public void addScheduledTask(ScheduledTask scheduledTask) {
		scheduledTaskDomainService.addScheduledTask(scheduledTask);
	}

	/**
	 * 删除定时任务
	 *
	 * @param taskId 任务ID
	 */
	public void deleteScheduledTask(Long taskId) {
		scheduledTaskDomainService.deleteScheduledTask(taskId);
	}

	/**
	 * 更新定时任务
	 *
	 * @param scheduledTask 定时任务领域对象
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateScheduledTask(ScheduledTask scheduledTask) {
		scheduledTaskDomainService.updateScheduledTask(scheduledTask);

	}

	/**
	 * 修改定时任务状态
	 *
	 * @param scheduledTask 定时任务领域对象
	 */
	@Transactional(rollbackFor = Exception.class)
	public void editTaskStatus(ScheduledTask scheduledTask) {
		scheduledTaskDomainService.editTaskStatus(scheduledTask);
	}

	/**
	 * 获取定时任务管理分页列表
	 *
	 * @param scheduledTask 定时任务领域对象
	 * @return 定时任务管理分页列表
	 */
	public PageVO<ScheduledTask> getScheduledTaskPage(ScheduledTask scheduledTask) {
		return scheduledTaskDomainService.getScheduledTaskPage(scheduledTask);
	}

	/**
	 * 刷新定时任务
	 */
	public void refreshScheduledTask() {
		scheduledTaskDomainService.refreshScheduledTask();
	}
}
