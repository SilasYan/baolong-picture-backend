package com.baolong.pictures.application.shared.websocket.disruptor;

import cn.hutool.json.JSONUtil;
import com.baolong.pictures.application.service.UserApplicationService;
import com.baolong.pictures.application.shared.websocket.PictureEditHandler;
import com.baolong.pictures.application.shared.websocket.model.PictureEditMessageTypeEnum;
import com.baolong.pictures.application.shared.websocket.model.PictureEditRequestMessage;
import com.baolong.pictures.application.shared.websocket.model.PictureEditResponseMessage;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.interfaces.web.user.assembler.UserAssembler;
import com.baolong.pictures.interfaces.web.user.response.UserDetailVO;
import com.lmax.disruptor.WorkHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.annotation.Resource;

/**
 * 图片编辑事件处理（事件消费者）
 */
@Slf4j
@Component
public class PictureEditEventWorkHandler implements WorkHandler<PictureEditEvent> {

    @Resource
    @Lazy
    private PictureEditHandler pictureEditHandler;

    @Resource
    private UserApplicationService userApplicationService;

    @Override
    public void onEvent(PictureEditEvent event) throws Exception {
        PictureEditRequestMessage pictureEditRequestMessage = event.getPictureEditRequestMessage();
        WebSocketSession session = event.getSession();
        User user = event.getUser();
        Long pictureId = event.getPictureId();
        // 获取到消息类别
        String type = pictureEditRequestMessage.getType();
        PictureEditMessageTypeEnum pictureEditMessageTypeEnum = PictureEditMessageTypeEnum.valueOf(type);
        // 调用对应的消息处理方法
        switch (pictureEditMessageTypeEnum) {
            case ENTER_EDIT:
                pictureEditHandler.handleEnterEditMessage(pictureEditRequestMessage, session, user, pictureId);
                break;
            case EDIT_ACTION:
                pictureEditHandler.handleEditActionMessage(pictureEditRequestMessage, session, user, pictureId);
                break;
            case EXIT_EDIT:
                pictureEditHandler.handleExitEditMessage(pictureEditRequestMessage, session, user, pictureId);
                break;
            case COMPLETE:
                pictureEditHandler.handleCompleteMessage(pictureEditRequestMessage, session, user, pictureId);
                break;
            default:
                PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
                pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.ERROR.getValue());
                pictureEditResponseMessage.setMessage("消息类型错误");
                UserDetailVO userDetailVO = UserAssembler.toUserDetailVO(userApplicationService.getUserDetailById(user.getUserId()));
                pictureEditResponseMessage.setUser(userDetailVO);
                session.sendMessage(new TextMessage(JSONUtil.toJsonStr(pictureEditResponseMessage)));
        }
    }
}
