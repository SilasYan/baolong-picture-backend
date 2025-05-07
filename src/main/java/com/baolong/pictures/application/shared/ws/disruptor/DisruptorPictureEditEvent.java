package com.baolong.pictures.application.shared.ws.disruptor;

import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.interfaces.web.picture.request.PictureEditMessageRequest;
import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

/**
 * 无锁队列图片编辑事件
 *
 * @author Silas Yan 2025-05-06 21:33
 */
@Data
public class DisruptorPictureEditEvent {

	/**
	 * 当前事件的会话
	 */
	private WebSocketSession session;

	/**
	 * 当前用户
	 */
	private User user;

	/**
	 * 图片ID
	 */
	private Long pictureId;

	/**
	 * 图片编辑消息请求
	 */
	private PictureEditMessageRequest pictureEditMessageRequest;
}
