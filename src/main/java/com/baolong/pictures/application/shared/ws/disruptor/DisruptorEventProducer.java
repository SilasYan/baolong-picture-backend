package com.baolong.pictures.application.shared.ws.disruptor;

import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.interfaces.web.picture.request.PictureEditMessageRequest;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * 无锁队列事件生产者
 *
 * @author Silas Yan 2025-05-06 21:32
 */
@Slf4j
@Component
public class DisruptorEventProducer {

	@Resource
	Disruptor<DisruptorPictureEditEvent> pictureEditEventDisruptor;

	/**
	 * 发布事件
	 *
	 * @param pictureEditMessageRequest 图片编辑消息请求
	 * @param session               事件会话
	 * @param user                  用户信息
	 * @param pictureId             图片ID
	 */
	public void publishEvent(PictureEditMessageRequest pictureEditMessageRequest, WebSocketSession session, User user, Long pictureId) {
		RingBuffer<DisruptorPictureEditEvent> ringBuffer = pictureEditEventDisruptor.getRingBuffer();
		// 获取可以生成的位置
		long next = ringBuffer.next();
		DisruptorPictureEditEvent disruptorPictureEditEvent = ringBuffer.get(next);
		disruptorPictureEditEvent.setSession(session);
		disruptorPictureEditEvent.setPictureEditMessageRequest(pictureEditMessageRequest);
		disruptorPictureEditEvent.setUser(user);
		disruptorPictureEditEvent.setPictureId(pictureId);
		// 发布事件
		ringBuffer.publish(next);
	}

	/**
	 * 优雅停机
	 */
	@PreDestroy
	public void close() {
		pictureEditEventDisruptor.shutdown();
	}
}
