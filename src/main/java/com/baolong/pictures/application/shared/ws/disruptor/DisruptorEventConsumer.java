package com.baolong.pictures.application.shared.ws.disruptor;

import cn.hutool.json.JSONUtil;
import com.baolong.pictures.application.shared.ws.WsPictureEditHandler;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureEditMessageTypeEnum;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.interfaces.web.picture.request.PictureEditMessageRequest;
import com.baolong.pictures.interfaces.web.picture.response.PictureEditMessageVO;
import com.baolong.pictures.interfaces.web.user.assembler.UserAssembler;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * 无锁队列事件消费者
 *
 * @author Silas Yan 2025-05-06 21:46
 */
@Slf4j
@Component
public class DisruptorEventConsumer implements WorkHandler<DisruptorPictureEditEvent> {

	@Lazy
	@Resource
	private WsPictureEditHandler wsPictureEditHandler;

	/**
	 * Callback to indicate a unit of work needs to be processed.
	 *
	 * @param event published to the {@link RingBuffer}
	 * @throws Exception if the {@link WorkHandler} would like the exception handled further up the chain.
	 */
	@Override
	public void onEvent(DisruptorPictureEditEvent event) throws Exception {
		PictureEditMessageRequest pictureEditMessageRequest = event.getPictureEditMessageRequest();
		WebSocketSession session = event.getSession();
		User user = event.getUser();
		Long pictureId = event.getPictureId();
		// 获取当前操作的类型
		String type = pictureEditMessageRequest.getType();
		switch (PictureEditMessageTypeEnum.valueOf(type)) {
			case ENTER_EDIT:
				wsPictureEditHandler.handleEnterEditMessage(pictureEditMessageRequest, session, user, pictureId);
				break;
			case EDIT_ACTION:
				wsPictureEditHandler.handleEditActionMessage(pictureEditMessageRequest, session, user, pictureId);
				break;
			case EXIT_EDIT:
				wsPictureEditHandler.handleExitEditMessage(pictureEditMessageRequest, session, user, pictureId);
				break;
			case COMPLETE:
				wsPictureEditHandler.handleCompleteEditMessage(pictureEditMessageRequest, session, user, pictureId);
				break;
			default:
				PictureEditMessageVO pictureEditMessageVO = new PictureEditMessageVO();
				pictureEditMessageVO.setType(PictureEditMessageTypeEnum.ERROR.getKey());
				pictureEditMessageVO.setMessage("消息类型错误");
				pictureEditMessageVO.setUser(UserAssembler.toUserVO(user));
				session.sendMessage(new TextMessage(JSONUtil.toJsonStr(pictureEditMessageVO)));
		}
	}
}
