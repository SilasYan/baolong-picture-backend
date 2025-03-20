package com.baolong.pictures.infrastructure.manager.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Redis 属性类
 *
 * @author Baolong 2025年03月06 22:52
 * @version 1.0
 * @since 1.8
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.redis")
public class RedisProperties {
	private String host;
	private String password;
	private Integer port;
	private Integer database;
	private Integer timeout;
}
