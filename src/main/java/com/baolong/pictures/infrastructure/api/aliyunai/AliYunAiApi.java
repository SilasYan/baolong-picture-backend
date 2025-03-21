package com.baolong.pictures.infrastructure.api.aliyunai;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.infrastructure.api.aliyunai.model.CreateOutPaintingTaskRequest;
import com.baolong.pictures.infrastructure.api.aliyunai.model.CreateOutPaintingTaskResponse;
import com.baolong.pictures.infrastructure.api.aliyunai.model.GetOutPaintingTaskResponse;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 阿里云 AI 接口
 */
@Slf4j
@Component
public class AliYunAiApi {
	// 读取配置文件
	@Value("${aliYunAi.apiKey}")
	private String apiKey;

	// 创建任务地址
	public static final String CREATE_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

	// 查询任务状态
	public static final String GET_OUT_PAINTING_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

	/**
	 * 创建任务
	 *
	 * @param createOutPaintingTaskRequest 图片扩图任务请求对象
	 * @return 扩图任务响应对象
	 */
	public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest createOutPaintingTaskRequest) {
		if (createOutPaintingTaskRequest == null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "扩图参数为空");
		}
		// 发送请求
		HttpRequest httpRequest = HttpRequest.post(CREATE_OUT_PAINTING_TASK_URL)
				.header(Header.AUTHORIZATION, "Bearer " + apiKey)
				// 必须开启异步处理，设置为enable。
				.header("X-DashScope-Async", "enable")
				.header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
				.body(JSONUtil.toJsonStr(createOutPaintingTaskRequest));
		try (HttpResponse httpResponse = httpRequest.execute()) {
			if (!httpResponse.isOk()) {
				log.error("请求异常：{}", httpResponse.body());
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
			}
			CreateOutPaintingTaskResponse response = JSONUtil.toBean(httpResponse.body(), CreateOutPaintingTaskResponse.class);
			String errorCode = response.getCode();
			if (StrUtil.isNotBlank(errorCode)) {
				String errorMessage = response.getMessage();
				log.error("AI 扩图失败，errorCode:{}, errorMessage:{}", errorCode, errorMessage);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图接口响应异常");
			}
			return response;
		}
	}

	/**
	 * 查询创建的任务
	 *
	 * @param taskId 任务ID
	 * @return 扩图任务响应对象
	 */
	public GetOutPaintingTaskResponse getOutPaintingTask(String taskId) {
		if (StrUtil.isBlank(taskId)) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务 id 不能为空");
		}
		try (HttpResponse httpResponse = HttpRequest.get(String.format(GET_OUT_PAINTING_TASK_URL, taskId))
				.header(Header.AUTHORIZATION, "Bearer " + apiKey)
				.execute()) {
			if (!httpResponse.isOk()) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务失败");
			}
			return JSONUtil.toBean(httpResponse.body(), GetOutPaintingTaskResponse.class);
		}
	}
}
