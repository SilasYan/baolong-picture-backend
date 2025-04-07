package com.baolong.pictures.interfaces.rest.system;

import cn.hutool.core.util.ObjectUtil;
import com.baolong.pictures.application.service.system.ScheduledTaskApplicationService;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.system.task.aggregate.ScheduledTask;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.system.task.assembler.ScheduledTaskAssembler;
import com.baolong.pictures.interfaces.web.system.task.request.ScheduledTaskAddRequest;
import com.baolong.pictures.interfaces.web.system.task.request.ScheduledTaskQueryRequest;
import com.baolong.pictures.interfaces.web.system.task.request.ScheduledTaskUpdateRequest;
import com.baolong.pictures.interfaces.web.system.task.response.ScheduledTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 定时任务表 (scheduled_task) - 接口
 *
 * @author Silas Yan 2025-03-22:15:57
 */
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class ScheduledTaskController {
	private final ScheduledTaskApplicationService scheduledTaskApplicationService;

	/**
	 * 新增定时任务
	 *
	 * @param scheduledTaskAddRequest 定时任务新增请求
	 * @return 新增结果
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> addScheduledTask(@RequestBody ScheduledTaskAddRequest scheduledTaskAddRequest) {
		ThrowUtils.throwIf(scheduledTaskAddRequest == null, ErrorCode.PARAMS_ERROR);
		ScheduledTask scheduledTask = ScheduledTaskAssembler.toDomain(scheduledTaskAddRequest);
		scheduledTask.checkParam();
		scheduledTaskApplicationService.addScheduledTask(scheduledTask);
		return ResultUtils.success();
	}

	/**
	 * 删除定时任务
	 *
	 * @param deleteRequest 定时任务删除请求
	 * @return 删除结果
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteScheduledTask(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
		Long scheduledTaskId = deleteRequest.getId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(scheduledTaskId), ErrorCode.PARAMS_ERROR);
		scheduledTaskApplicationService.deleteScheduledTask(scheduledTaskId);
		return ResultUtils.success();
	}

	/**
	 * 更新定时任务
	 *
	 * @param scheduledTaskUpdateRequest 定时任务更新请求
	 * @return 更新结果
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateScheduledTask(@RequestBody ScheduledTaskUpdateRequest scheduledTaskUpdateRequest) {
		ThrowUtils.throwIf(scheduledTaskUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(scheduledTaskUpdateRequest.getTaskId()), ErrorCode.PARAMS_ERROR);
		ScheduledTask scheduledTask = ScheduledTaskAssembler.toDomain(scheduledTaskUpdateRequest);
		scheduledTask.checkParam();
		scheduledTaskApplicationService.updateScheduledTask(scheduledTask);
		return ResultUtils.success();
	}

	/**
	 * 修改定时任务状态
	 *
	 * @param scheduledTaskUpdateRequest 定时任务更新请求
	 * @return 修改结果
	 */
	@PostMapping("/editTaskStatus")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> editTaskStatus(@RequestBody ScheduledTaskUpdateRequest scheduledTaskUpdateRequest) {
		ThrowUtils.throwIf(scheduledTaskUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		Long taskId = scheduledTaskUpdateRequest.getTaskId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(taskId), ErrorCode.PARAMS_ERROR);
		Integer taskStatus = scheduledTaskUpdateRequest.getTaskStatus();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(taskStatus), ErrorCode.PARAMS_ERROR);
		scheduledTaskApplicationService.editTaskStatus(ScheduledTaskAssembler.toDomain(scheduledTaskUpdateRequest));
		return ResultUtils.success();
	}

	/**
	 * 获取定时任务管理分页列表
	 *
	 * @param scheduledTaskQueryRequest 定时任务查询请求
	 * @return 定时任务管理分页列表
	 */
	@PostMapping("/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<ScheduledTaskVO>> getScheduledTaskPage(
			@RequestBody ScheduledTaskQueryRequest scheduledTaskQueryRequest) {
		ThrowUtils.throwIf(scheduledTaskQueryRequest == null, ErrorCode.PARAMS_ERROR);
		ScheduledTask scheduledTask = ScheduledTaskAssembler.toDomain(scheduledTaskQueryRequest);
		PageVO<ScheduledTask> scheduledTaskPageVO = scheduledTaskApplicationService.getScheduledTaskPage(scheduledTask);
		return ResultUtils.success(ScheduledTaskAssembler.toPageVO(scheduledTaskPageVO));
	}

	/**
	 * 刷新定时任务
	 *
	 * @return 刷新结果
	 */
	@PostMapping("refresh")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> refreshScheduledTask() {
		scheduledTaskApplicationService.refreshScheduledTask();
		return ResultUtils.success();
	}
}
