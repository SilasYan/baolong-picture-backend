package com.baolong.pictures.infrastructure.api.bailian.model;

import lombok.Data;

/**
 * 百炼任务响应类
 *
 * @author Silas Yan 2025-03-24:22:46
 */
@Data
public class BaiLianTaskResponse {

	/**
	 * 请求唯一标识。可用于请求明细溯源和问题排查。
	 */
	private String requestId;

	/**
	 * 任务输出信息
	 */
	private Output output;

	/**
	 * 图像统计信息
	 */
	private Output Usage;

	/**
	 * 输出的任务信息
	 */
	@Data
	public static class Output {

		/**
		 * 任务 ID
		 */
		private String taskId;

		/**
		 * 任务状态
		 * <p>
		 * PENDING：任务排队中
		 * <p>
		 * RUNNING：任务处理中
		 * <p>
		 * SUSPENDED：任务挂起
		 * <p>
		 * SUCCEEDED：任务执行成功
		 * <p>
		 * FAILED：任务执行失败
		 * <p>
		 * UNKNOWN：任务不存在或状态未知
		 */
		private String taskStatus;

		/**
		 * 任务结果统计
		 */
		private TaskMetrics taskMetrics;

		/**
		 * 任务提交时间 格式：YYYY-MM-DD HH:mm:ss.SSS
		 */
		private String submitTime;

		/**
		 * 任务完成时间 格式：YYYY-MM-DD HH:mm:ss.SSS
		 */
		private String endTime;

		/**
		 * 输出图像URL地址
		 */
		private String outputImageUrl;

		/**
		 * 请求失败的错误码。请求成功时不会返回此参数，详情请参见错误信息。
		 * <p>
		 * https://help.aliyun.com/zh/model-studio/developer-reference/error-code?spm=a2c4g.11186623.0.0.45e268bb6xYLbJ
		 */
		private String code;

		/**
		 * 请求失败的详细信息。请求成功时不会返回此参数，详情请参见错误信息。
		 * <p>
		 * https://help.aliyun.com/zh/model-studio/developer-reference/error-code?spm=a2c4g.11186623.0.0.45e268bb6xYLbJ
		 */
		private String message;

		/**
		 * 调度时间 格式：YYYY-MM-DD HH:mm:ss.SSS
		 */
		private String scheduledTime;
	}

	/**
	 * 任务结果统计
	 */
	@Data
	public static class TaskMetrics {

		/**
		 * 总的任务数
		 */
		private Integer total;

		/**
		 * 任务状态为成功的任务数
		 */
		private Integer succeeded;

		/**
		 * 任务状态为失败的任务数
		 */
		private Integer failed;
	}

	/**
	 * 图像统计信息
	 */
	@Data
	public static class Usage {

		/**
		 * 模型生成图片的数量
		 */
		private String image_count;
	}
}
