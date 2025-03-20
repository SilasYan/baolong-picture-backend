package com.baolong.pictures.application.shared.websocket.disruptor;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 图片编辑事件 disruptor 配置
 */
@Configuration
public class PictureEditEventDisruptorConfig {

	@Resource
	private PictureEditEventWorkHandler pictureEditEventWorkHandler;

	@Bean("pictureEditEventDisruptor")
	public Disruptor<PictureEditEvent> messageModelRingBuffer() {
		// ringBuffer 的大小
		int bufferSize = 1024 * 256;
		Disruptor<PictureEditEvent> disruptor = new Disruptor<>(
				PictureEditEvent::new,
				bufferSize,
				ThreadFactoryBuilder.create().setNamePrefix("pictureEditEventDisruptor").build()
		);
		// 设置消费者
		disruptor.handleEventsWithWorkerPool(pictureEditEventWorkHandler);
		// 开启 disruptor
		disruptor.start();
		return disruptor;
	}
}
