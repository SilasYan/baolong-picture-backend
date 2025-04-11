package com.baolong.pictures.infrastructure.api.bailian;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.baolong.pictures.infrastructure.api.bailian.config.BaiLianConfig;
import com.baolong.pictures.infrastructure.api.bailian.model.BaiLianTaskResponse;
import com.baolong.pictures.infrastructure.api.bailian.model.CreateBaiLianTaskResponse;
import com.baolong.pictures.infrastructure.api.bailian.model.ExpandImageTaskRequest;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * 百炼
 */
@Slf4j
@Component
public class BaiLianApi {

	@Resource
	private BaiLianConfig baiLianConfig;
	@Resource
	private OkHttpClient okHttpClient;

	/**
	 * 查询百炼任务
	 *
	 * @param taskId 任务ID
	 * @return 扩图任务响应对象
	 */
	public BaiLianTaskResponse queryBaiLianTask(String taskId) {
		log.info("[百炼大模型]查询任务, 任务ID[{}]", taskId);
		Request request = new Request.Builder()
				.url(String.format(BaiLianConfig.GET_TASK_URL, taskId))
				.header("Authorization", baiLianConfig.bearer())
				.build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			// 检查响应是否成功
			if (!response.isSuccessful()) {
				log.error("[百炼大模型]查询失败, HTTP状态码: {}", response.code());
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务失败");
			}

			// 检查响应体是否为空
			ResponseBody body = response.body();
			if (body == null) {
				log.error("[百炼大模型]查询失败, 响应体为空");
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务失败");
			}

			try {
				// 读取响应体内容
				String responseBody = body.string();
				log.debug("[百炼大模型]原始响应: {}", responseBody);  // 调试用，记录原始响应
				// 解析JSON
				BaiLianTaskResponse taskResponse = JSONUtil.toBean(responseBody, BaiLianTaskResponse.class);
				log.info("[百炼大模型]查询结果: {}", JSONUtil.toJsonPrettyStr(taskResponse));
				// 检查错误码
				if (taskResponse.getOutput() == null) {
					log.error("[百炼大模型]查询失败, 输出内容为空");
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务数据异常");
				}
				String errCode = taskResponse.getOutput().getCode();
				if (StrUtil.isNotEmpty(errCode)) {
					String errorMessage = taskResponse.getOutput().getMessage();
					log.error("[百炼大模型]查询失败, 状态码: [{}], 详情: {}", errCode, errorMessage);
					// 处理特定错误消息
					if (errorMessage != null) {
						if (errorMessage.contains("the size of input image is too small or to large")) {
							throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片尺寸过小或过大");
						}
						if (errorMessage.contains("the num of pixel extension is 0")) {
							throw new BusinessException(ErrorCode.OPERATION_ERROR, "未指定扩图的参数");
						}
					}
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务查询失败");
				}
				log.info("[百炼大模型]查询完成...");
				return taskResponse;
			} catch (JSONException e) {
				log.error("[百炼大模型]JSON解析失败", e);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务数据解析失败");
			} catch (IOException e) {
				log.error("[百炼大模型]读取响应体失败", e);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务数据失败");
			}
		} catch (IOException e) {
			log.error("[百炼大模型]网络请求失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "连接服务失败");
		}
		/*try {
			Response response = okHttpClient.newCall(request).execute();
			if (!response.isSuccessful()) {
				log.error("[百炼大模型]查询失败, {}", response);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务失败");
			}
			String responseBody = response.body().toString();
			BaiLianTaskResponse taskResponse = JSONUtil.toBean(responseBody, BaiLianTaskResponse.class);
			log.info("[百炼大模型]查询结果: {}", JSONUtil.parse(taskResponse));
			String errCode = taskResponse.getOutput().getCode();
			if (StrUtil.isNotEmpty(errCode)) {
				log.error("[百炼大模型]查询失败, 状态码: [{}], 详情: {}", errCode, taskResponse.getOutput().getMessage());
				if ("the size of input image is too small or to large".contains(taskResponse.getOutput().getMessage())) {
					// the size of input image is too small or to large
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "图片尺寸过小或过大");
				}
				if ("the num of pixel extension is 0".contains(taskResponse.getOutput().getMessage())) {
					// the size of input image is too small or to large
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "未指定扩图的参数");
				}
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务查询失败");
			}
			log.info("[百炼大模型]查询完成...");
			return taskResponse;
		} catch (IOException e) {
			log.error("[百炼大模型]查询失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务查询失败");
		}*/
	}

	/**
	 * 创建扩图任务
	 *
	 * @param expandImageTaskRequest 扩图请求对象
	 * @return 扩图任务响应对象
	 */
	public CreateBaiLianTaskResponse createExpandImageTask(ExpandImageTaskRequest expandImageTaskRequest) {
		log.info("[百炼大模型]开始创建扩图任务...");
		MediaType mediaType = MediaType.get("application/json; charset=utf-8");
		RequestBody requestBody = RequestBody.create(JSONUtil.toJsonStr(expandImageTaskRequest), mediaType);
		Request request = new Request.Builder()
				.url(BaiLianConfig.EXPAND_IMAGE_URL)
				.header("X-DashScope-Async", "enable")
				.header("Authorization", baiLianConfig.bearer())
				.post(requestBody)
				.build();
		try (Response response = okHttpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				log.error("[百炼大模型]扩图失败, {}", response);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
			}

			ResponseBody body = response.body();
			if (body == null) {
				log.error("[百炼大模型]扩图失败, 响应体为空");
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
			}

			try {
				String responseBody = body.string();
				CreateBaiLianTaskResponse taskResponse = JSONUtil.toBean(responseBody, CreateBaiLianTaskResponse.class);
				log.info("[百炼大模型]扩图结果: {}", JSONUtil.parse(taskResponse));

				String errorCode = taskResponse.getCode();
				if (StrUtil.isNotBlank(errorCode)) {
					log.error("[百炼大模型]扩图失败, 状态码: [{}], 详情: {}", errorCode, taskResponse.getMessage());
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
				}

				log.info("[百炼大模型]扩图任务创建成功...");
				return taskResponse;
			} catch (IOException e) {
				log.error("[百炼大模型]读取响应体失败", e);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "扩图失败");
			} catch (Exception e) {
				log.error("[百炼大模型]JSON解析失败", e);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "扩图失败");
			}
		} catch (IOException e) {
			log.error("[百炼大模型]扩图失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "扩图失败");
		}
		/*try {
			Response response = okHttpClient.newCall(request).execute();
			if (!response.isSuccessful()) {
				log.error("[百炼大模型]扩图失败, {}", response);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
			}
			String responseBody = response.body().toString();
			CreateBaiLianTaskResponse taskResponse = JSONUtil.toBean(responseBody, CreateBaiLianTaskResponse.class);
			log.info("[百炼大模型]扩图结果: {}", JSONUtil.parse(taskResponse));
			String errorCode = taskResponse.getCode();
			if (StrUtil.isNotBlank(errorCode)) {
				log.error("[百炼大模型]扩图失败, 状态码: [{}], 详情: {}", errorCode, taskResponse.getMessage());
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
			}
			log.info("[百炼大模型]扩图任务创建成功...");
			return taskResponse;
		} catch (IOException e) {
			log.error("[百炼大模型]扩图失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "扩图失败");
		}*/
		/*HttpRequest httpRequest = HttpRequest.post(BaiLianConfig.EXPAND_IMAGE_URL)
				.header(Header.AUTHORIZATION, baiLianConfig.bearer())
				.header("X-DashScope-Async", "enable")
				.header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
				.body(JSONUtil.toJsonStr(expandImageTaskRequest));
		try (HttpResponse response = httpRequest.execute()) {
			if (!response.isOk()) {
				log.error("[百炼大模型]扩图失败, {}", response);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
			}
			String body = response.body();
			if (body == null) {
				log.error("[百炼大模型]扩图失败, {}", response);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
			}
			log.info("[百炼大模型]扩图结果: {}", body);
			CreateBaiLianTaskResponse taskResponse = JSONUtil.toBean(body, CreateBaiLianTaskResponse.class);
			String errorCode = taskResponse.getCode();
			if (StrUtil.isNotBlank(errorCode)) {
				log.error("[百炼大模型]扩图失败, 状态码: [{}], 详情: {}", errorCode, taskResponse.getMessage());
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "AI 扩图失败");
			}
			log.info("[百炼大模型]扩图任务创建成功...");
			return taskResponse;
		}*/
	}

	/**
	 * 创建文生图任务
	 *
	 * @param prompt 提示词
	 * @param size   大小
	 * @return 任务响应对象
	 */
	public BaiLianTaskResponse createTextGenerateImageTask(String prompt, String size) {
		log.info("[百炼大模型]开始创建文生图任务...");
		try {
			ImageSynthesisParam param = ImageSynthesisParam.builder()
					.apiKey(baiLianConfig.getApiKey()).model("wanx2.1-t2i-turbo").n(1)
					.prompt(prompt)
					.size(size)
					.build();
			ImageSynthesis imageSynthesis = new ImageSynthesis();
			log.info("[百炼大模型]文生图调用成功, 等待结果中...");
			ImageSynthesisResult result = imageSynthesis.call(param);
			log.info("[百炼大模型]文生图结果: {}", JSONUtil.parse(result));
			BaiLianTaskResponse baiLianTaskResponse = new BaiLianTaskResponse();
			BeanUtils.copyProperties(result, baiLianTaskResponse);
			Map<String, String> resultMap = result.getOutput().getResults().get(0);
			BaiLianTaskResponse.Results results = new BaiLianTaskResponse.Results();
			results.setOrigPrompt(resultMap.get("orig_prompt"));
			results.setActualPrompt(resultMap.get("actual_prompt"));
			results.setUrl(resultMap.get("url"));
			baiLianTaskResponse.setResults(results);
			log.info("[百炼大模型]响应结果: {}", JSONUtil.parse(baiLianTaskResponse));
			return baiLianTaskResponse;
		} catch (Exception e) {
			log.error("[百炼大模型]文生图失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "文生图失败");
		}
	}
}
