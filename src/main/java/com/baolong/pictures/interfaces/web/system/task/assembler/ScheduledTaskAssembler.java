package com.baolong.pictures.interfaces.web.system.task.assembler;

import com.baolong.pictures.domain.system.task.aggregate.ScheduledTask;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.system.task.request.ScheduledTaskAddRequest;
import com.baolong.pictures.interfaces.web.system.task.request.ScheduledTaskQueryRequest;
import com.baolong.pictures.interfaces.web.system.task.request.ScheduledTaskUpdateRequest;
import com.baolong.pictures.interfaces.web.system.task.response.ScheduledTaskVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 定时任务转换类
 *
 * @author Baolong 2025年03月04 23:37
 * @version 1.0
 * @since 1.8
 */
public class ScheduledTaskAssembler {

	/**
	 * 定时任务更新请求 转为 定时任务领域对象
	 */
	public static ScheduledTask toDomain(ScheduledTaskAddRequest scheduledTaskAddRequest) {
		ScheduledTask scheduledTask = new ScheduledTask();
		BeanUtils.copyProperties(scheduledTaskAddRequest, scheduledTask);
		return scheduledTask;
	}

	/**
	 * 定时任务更新请求 转为 定时任务领域对象
	 */
	public static ScheduledTask toDomain(ScheduledTaskUpdateRequest scheduledTaskUpdateRequest) {
		ScheduledTask scheduledTask = new ScheduledTask();
		BeanUtils.copyProperties(scheduledTaskUpdateRequest, scheduledTask);
		return scheduledTask;
	}

	/**
	 * 定时任务查询请求 转为 定时任务领域对象
	 */
	public static ScheduledTask toDomain(ScheduledTaskQueryRequest scheduledTaskQueryRequest) {
		ScheduledTask scheduledTask = new ScheduledTask();
		BeanUtils.copyProperties(scheduledTaskQueryRequest, scheduledTask);
		return scheduledTask;
	}

	/**
	 * 定时任务领域对象 转为 定时任务响应对象
	 */
	public static ScheduledTaskVO toVO(ScheduledTask scheduledTask) {
		ScheduledTaskVO scheduledTaskVO = new ScheduledTaskVO();
		BeanUtils.copyProperties(scheduledTask, scheduledTaskVO);
		return scheduledTaskVO;
	}

	/**
	 * 定时任务领域对象分页 转为 定时任务响应对象分页
	 */
	public static PageVO<ScheduledTaskVO> toPageVO(PageVO<ScheduledTask> scheduledTaskPageVO) {
		return new PageVO<>(scheduledTaskPageVO.getCurrent()
				, scheduledTaskPageVO.getPageSize()
				, scheduledTaskPageVO.getTotal()
				, scheduledTaskPageVO.getPages()
				, Optional.ofNullable(scheduledTaskPageVO.getRecords())
				.orElse(List.of()).stream()
				.map(ScheduledTaskAssembler::toVO)
				.collect(Collectors.toList())
		);
	}
}
