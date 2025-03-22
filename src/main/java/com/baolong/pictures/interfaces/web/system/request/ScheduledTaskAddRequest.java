package com.baolong.pictures.interfaces.web.system.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 定时任务新增请求
 */
@Data
public class ScheduledTaskAddRequest implements Serializable {

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

	private static final long serialVersionUID = 1L;
}
