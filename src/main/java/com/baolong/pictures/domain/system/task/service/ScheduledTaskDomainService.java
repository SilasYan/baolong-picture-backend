package com.baolong.pictures.domain.system.task.service;

import com.baolong.pictures.domain.system.task.aggregate.ScheduledTask;
import com.baolong.pictures.domain.system.task.aggregate.enums.TaskStatusEnum;
import com.baolong.pictures.domain.system.task.repository.ScheduledTaskRepository;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.manager.task.ScheduledTaskManger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 定时任务表 (scheduled_task) - 领域服务
 *
 * @author Silas Yan 2025-03-22:15:53
 */
@Service
@RequiredArgsConstructor
public class ScheduledTaskDomainService {

	private final ScheduledTaskRepository scheduledTaskRepository;
	private final ScheduledTaskManger scheduledTaskManger;

	/**
	 * 新增定时任务
	 *
	 * @param scheduledTask 定时任务领域对象
	 */
	public void addScheduledTask(ScheduledTask scheduledTask) {
		boolean existed = scheduledTaskRepository.existedScheduledTaskByTaskKeyOrTaskBean(scheduledTask.getTaskBean());
		if (existed) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "任务方法已存在!");
		}
		scheduledTask.setTaskKey(scheduledTask.getTaskBean());
		boolean result = scheduledTaskRepository.addScheduledTask(scheduledTask);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增定时任务失败!");
	}

	/**
	 * 删除定时任务
	 *
	 * @param taskId 任务ID
	 */
	public void deleteScheduledTask(Long taskId) {
		boolean existed = scheduledTaskRepository.existedScheduledTaskByTaskId(taskId);
		if (!existed) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "定时任务不存在!");
		}
		boolean result = scheduledTaskRepository.deleteScheduledTask(taskId);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除定时任务失败!");
	}

	/**
	 * 更新定时任务
	 *
	 * @param scheduledTask 定时任务领域对象
	 */
	public void updateScheduledTask(ScheduledTask scheduledTask) {
		Long taskId = scheduledTask.getTaskId();
		boolean existed = scheduledTaskRepository.existedScheduledTaskByTaskId(taskId);
		if (!existed) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "定时任务不存在!");
		}
		ScheduledTask oldScheduledTask = scheduledTaskRepository.getScheduledTaskByTaskBean(scheduledTask.getTaskBean());
		if (oldScheduledTask != null && !oldScheduledTask.getTaskId().equals(taskId)) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "任务方法已存在!");
		}
		boolean result = scheduledTaskRepository.updateScheduledTask(scheduledTask);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新定时任务失败!");
	}

	/**
	 * 修改定时任务状态
	 *
	 * @param scheduledTask 定时任务领域对象
	 */
	public void editTaskStatus(ScheduledTask scheduledTask) {
		Long taskId = scheduledTask.getTaskId();
		boolean existed = scheduledTaskRepository.existedScheduledTaskByTaskId(taskId);
		if (!existed) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "定时任务不存在!");
		}
		boolean result = scheduledTaskRepository.updateScheduledTask(scheduledTask);
		if (result) {
			scheduledTaskManger.refresh(taskId);
			return;
		}
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "定时任务" +
				(TaskStatusEnum.isOpen(scheduledTask.getTaskStatus()) ? "开启" : "关闭")
				+ "失败!");
	}

	/**
	 * 获取定时任务管理分页列表
	 *
	 * @param scheduledTask 定时任务领域对象
	 * @return 定时任务管理分页列表
	 */
	public PageVO<ScheduledTask> getScheduledTaskPage(ScheduledTask scheduledTask) {
		return scheduledTaskRepository.getScheduledTaskPage(scheduledTask);
	}

	/**
	 * 刷新定时任务
	 */
	public void refreshScheduledTask() {
		scheduledTaskManger.refresh();
	}
}
