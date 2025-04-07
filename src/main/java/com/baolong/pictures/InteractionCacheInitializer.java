package com.baolong.pictures;

import com.baolong.pictures.infrastructure.cache.PictureInteractionCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 初始化
 *
 * @author Silas Yan 2025-03-29:17:57
 */
@Slf4j
@Component
public class InteractionCacheInitializer implements ApplicationRunner {

	@Resource
	private PictureInteractionCache pictureInteractionCache;

	@Override
	public void run(ApplicationArguments args) {
		log.info("↓↓↓↓↓↓↓↓↓↓ 开始初始化缓存 ↓↓↓↓↓↓↓↓↓↓");
		try {
			pictureInteractionCache.init();
			log.info("↑↑↑↑↑↑↑↑↑↑ 初始化缓存成功 ↑↑↑↑↑↑↑↑↑↑");
		} catch (Exception e) {
			log.error("初始化缓存失败", e);
		}
	}
}
