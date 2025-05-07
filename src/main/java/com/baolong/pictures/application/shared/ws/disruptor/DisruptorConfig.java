package com.baolong.pictures.application.shared.ws.disruptor;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import com.lmax.disruptor.dsl.Disruptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 无锁队列配置
 *
 * @author Silas Yan 2025-05-06 21:32
 */
@Configuration
public class DisruptorConfig {

	@Resource
	private DisruptorEventConsumer disruptorEventConsumer;

	@Bean("pictureEditEventDisruptor")
	public Disruptor<DisruptorPictureEditEvent> messageModelRingBuffer() {
		// ringBuffer 的大小
		int bufferSize = 1024 * 256;
		Disruptor<DisruptorPictureEditEvent> disruptor = new Disruptor<>(
				DisruptorPictureEditEvent::new,
				bufferSize,
				ThreadFactoryBuilder.create().setNamePrefix("pictureEditEventDisruptor").build()
		);
		// 设置消费者
		disruptor.handleEventsWithWorkerPool(disruptorEventConsumer);
		// 开启 disruptor
		disruptor.start();
		return disruptor;
	}
}
