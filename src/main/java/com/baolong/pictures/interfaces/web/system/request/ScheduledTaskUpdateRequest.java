package com.baolong.pictures.interfaces.web.system.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 定时任务更新请求
 */
@Data
public class ScheduledTaskUpdateRequest implements Serializable {

	/**
	 * 任务ID
	 */
	private Long taskId;

	/**
	 * 任务 KEY（存在内存中）
	 */
	private String taskKey;

	/**
	 * 任务名称
	 */
	private String taskName;

	/**
	 * 任务 corn 表达式
	 */
	private String taskCron;

	/**
	 * 任务描述
	 */
	private String taskDesc;

	/**
	 * 任务 Bean 名称（执行任务的 bean）
	 */
	private String taskBean;

	/**
	 * 任务状态（0-关闭, 1-开启）
	 */
	private Integer taskStatus;

	private static final long serialVersionUID = 1L;
}
