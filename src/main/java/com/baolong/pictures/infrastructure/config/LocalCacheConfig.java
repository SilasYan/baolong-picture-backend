package com.baolong.pictures.infrastructure.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.util.concurrent.TimeUnit;

/**
 * 本地缓存配置
 *
 * @author Baolong 2025年03月19 23:53
 * @version 1.0
 * @since 1.8
 */
public class LocalCacheConfig {
	/**
	 * 首页图片本地缓存
	 */
	public static final Cache<String, String> HOME_PICTURE_LOCAL_CACHE =
			Caffeine.newBuilder().initialCapacity(512)
					// 最多一万条数据
					.maximumSize(10000L)
					// 缓存 5 分钟移除
					.expireAfterWrite(5L, TimeUnit.MINUTES)
					.build();
}
