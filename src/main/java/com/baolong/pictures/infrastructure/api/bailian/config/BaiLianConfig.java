package com.baolong.pictures.infrastructure.api.bailian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 百炼配置类
 * <p>
 * 文档:
 * https://help.aliyun.com/zh/model-studio/developer-reference/image-scaling-api?spm=a2c4g.11186623.0.0.5bfab0a8nRu1ll
 *
 * @author Silas Yan 2025-03-24:22:18
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "aliyun.bailian")
public class BaiLianConfig {

	private String apiKey;

	/**
	 * 查询任务地址
	 */
	public static final String GET_TASK_URL = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";

	/**
	 * 图像画面扩展地址
	 */
	public static final String EXPAND_IMAGE_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

	public String bearer() {
		return "Bearer " + apiKey;
	}

}
