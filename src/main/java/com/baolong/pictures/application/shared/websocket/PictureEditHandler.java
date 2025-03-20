package com.baolong.pictures.application.shared.websocket;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.application.service.impl.UserApplicationService;
import com.baolong.pictures.application.shared.websocket.disruptor.PictureEditEventProducer;
import com.baolong.pictures.application.shared.websocket.model.PictureEditActionEnum;
import com.baolong.pictures.application.shared.websocket.model.PictureEditMessageTypeEnum;
import com.baolong.pictures.application.shared.websocket.model.PictureEditRequestMessage;
import com.baolong.pictures.application.shared.websocket.model.PictureEditResponseMessage;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.interfaces.web.user.assembler.UserAssembler;
import com.baolong.pictures.interfaces.web.user.response.UserDetailVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 处理器（图片处理器）
 */
@Component
public class PictureEditHandler extends TextWebSocketHandler {
	@Resource
	private UserApplicationService userApplicationService;

	@Resource
	private PictureEditEventProducer pictureEditEventProducer;

	// 每张图片的编辑状态，key: pictureId, value: 当前正在编辑的用户 ID
	private final Map<Long, Long> pictureEditingUsers = new ConcurrentHashMap<>();

	// 保存所有连接的会话，key: pictureId, value: 用户会话集合
	private final Map<Long, Set<WebSocketSession>> pictureSessions = new ConcurrentHashMap<>();

	/**
	 * 连接建立后执行
	 *
	 * @param session WebSocket 会话
	 * @throws Exception e
	 */
	@Override
	public void afterConnectionEstablished(WebSocketSession session) throws Exception {
		// 保存会话到集合中
		User user = (User) session.getAttributes().get("user");
		Long pictureId = (Long) session.getAttributes().get("pictureId");
		pictureSessions.putIfAbsent(pictureId, ConcurrentHashMap.newKeySet());
		pictureSessions.get(pictureId).add(session);

		// 构造响应
		PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
		pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getValue());
		String message = String.format("用户【%s】加入编辑", user.getUserName());
		pictureEditResponseMessage.setMessage(message);

		UserDetailVO userDetailVO = UserAssembler.toUserDetailVO(userApplicationService.getUserDetailById(user.getId()));
		pictureEditResponseMessage.setUser(userDetailVO);

		// 获取到当前正在编辑的用户信息
		Long inUserId = pictureEditingUsers.get(pictureId);
		if (inUserId != null) {
			User inUser = userApplicationService.getUserDetailById(inUserId);
			UserDetailVO userDetailVO2 = UserAssembler.toUserDetailVO(userApplicationService.getUserDetailById(inUser.getId()));
			pictureEditResponseMessage.setInUser(userDetailVO2);
		}
		// 广播给同一张图片的用户
		broadcastToPicture(pictureId, pictureEditResponseMessage);
	}

	/**
	 * 处理消息
	 *
	 * @param session WebSocket 会话
	 * @param message 消息
	 * @throws Exception e
	 */
	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
		// 将消息解析为 PictureEditMessage
		PictureEditRequestMessage pictureEditRequestMessage = JSONUtil.toBean(message.getPayload(), PictureEditRequestMessage.class);

		// 从 Session 属性中获取公共参数
		Map<String, Object> attributes = session.getAttributes();
		User user = (User) attributes.get("user");
		Long pictureId = (Long) attributes.get("pictureId");

		// 生产消息
		pictureEditEventProducer.publishEvent(pictureEditRequestMessage, session, user, pictureId);
	}

	/**
	 * 连接关闭后执行
	 *
	 * @param session WebSocket 会话
	 * @param status  关闭状态
	 * @throws Exception e
	 */
	@Override
	public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
		Map<String, Object> attributes = session.getAttributes();
		Long pictureId = (Long) attributes.get("pictureId");
		User user = (User) attributes.get("user");
		// 移除当前用户的编辑状态
		handleExitEditMessage(null, session, user, pictureId);

		// 删除会话
		Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
		if (sessionSet != null) {
			sessionSet.remove(session);
			if (sessionSet.isEmpty()) {
				pictureSessions.remove(pictureId);
			}
		}

		// 响应
		PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
		pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.INFO.getValue());
		String message = String.format("用户【%s】离开编辑", user.getUserName());
		pictureEditResponseMessage.setMessage(message);
		UserDetailVO userDetailVO = UserAssembler.toUserDetailVO(userApplicationService.getUserDetailById(user.getId()));
		pictureEditResponseMessage.setUser(userDetailVO);
		broadcastToPicture(pictureId, pictureEditResponseMessage);
	}

	/**
	 * 处理确认编辑消息
	 *
	 * @param pictureEditRequestMessage
	 * @param session
	 * @param TUser
	 * @param pictureId
	 * @throws Exception
	 */
	public void handleEnterEditMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws Exception {
		// 没有用户正在编辑该图片，才能进入编辑
		if (!pictureEditingUsers.containsKey(pictureId)) {
			// 设置当前用户为编辑用户
			pictureEditingUsers.put(pictureId, user.getId());
			PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
			pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.ENTER_EDIT.getValue());
			String message = String.format("用户【%s】开始编辑图片", user.getUserName());
			pictureEditResponseMessage.setMessage(message);

			UserDetailVO userDetailVO = UserAssembler.toUserDetailVO(userApplicationService.getUserDetailById(user.getId()));
			pictureEditResponseMessage.setUser(userDetailVO);
			broadcastToPicture(pictureId, pictureEditResponseMessage);
		}
	}

	/**
	 * 处理编辑操作消息
	 *
	 * @param pictureEditRequestMessage
	 * @param session
	 * @param TUser
	 * @param pictureId
	 * @throws Exception
	 */
	public void handleEditActionMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws Exception {
		Long editingUserId = pictureEditingUsers.get(pictureId);
		String editAction = pictureEditRequestMessage.getEditAction();
		PictureEditActionEnum actionEnum = PictureEditActionEnum.getEnumByValue(editAction);
		if (actionEnum == null) {
			return;
		}
		// 确认是当前编辑者
		if (editingUserId != null && editingUserId.equals(user.getId())) {
			PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
			pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EDIT_ACTION.getValue());
			String message = String.format("用户【%s】执行%s", user.getUserName(), actionEnum.getText());
			pictureEditResponseMessage.setMessage(message);
			pictureEditResponseMessage.setEditAction(editAction);
			UserDetailVO userDetailVO = UserAssembler.toUserDetailVO(userApplicationService.getUserDetailById(user.getId()));
			pictureEditResponseMessage.setUser(userDetailVO);
			// 广播给除了当前客户端之外的其他用户，否则会造成重复编辑
			broadcastToPicture(pictureId, pictureEditResponseMessage, session);
		}
	}

	/**
	 * 处理退出编辑消息
	 *
	 * @param pictureEditRequestMessage
	 * @param session
	 * @param TUser
	 * @param pictureId
	 * @throws Exception
	 */
	public void handleExitEditMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws Exception {
		Long editingUserId = pictureEditingUsers.get(pictureId);
		if (editingUserId != null && editingUserId.equals(user.getId())) {
			// 移除当前用户的编辑状态
			pictureEditingUsers.remove(pictureId);
			// 构造响应，发送退出编辑的消息通知
			PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
			pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.EXIT_EDIT.getValue());
			String message = String.format("用户【%s】退出编辑图片", user.getUserName());
			pictureEditResponseMessage.setMessage(message);

			UserDetailVO userDetailVO = UserAssembler.toUserDetailVO(userApplicationService.getUserDetailById(user.getId()));
			pictureEditResponseMessage.setUser(userDetailVO);
			broadcastToPicture(pictureId, pictureEditResponseMessage);
		}
	}

	public void handleCompleteMessage(PictureEditRequestMessage pictureEditRequestMessage, WebSocketSession session, User user, Long pictureId) throws Exception {
		Long editingUserId = pictureEditingUsers.get(pictureId);
		if (editingUserId != null && editingUserId.equals(user.getId())) {
			// 构造响应，发送完成编辑的消息通知
			PictureEditResponseMessage pictureEditResponseMessage = new PictureEditResponseMessage();
			pictureEditResponseMessage.setType(PictureEditMessageTypeEnum.COMPLETE.getValue());
			String message = String.format("用户【%s】完成图片编辑", user.getUserName());
			pictureEditResponseMessage.setMessage(message);

			UserDetailVO userDetailVO = UserAssembler.toUserDetailVO(userApplicationService.getUserDetailById(user.getId()));
			pictureEditResponseMessage.setUser(userDetailVO);
			broadcastToPicture(pictureId, pictureEditResponseMessage, session);
		}
	}

	/**
	 * 全部广播
	 *
	 * @param pictureId                  图片 ID
	 * @param pictureEditResponseMessage 编辑图片响应消息
	 * @throws Exception e
	 */
	private void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage) throws Exception {
		broadcastToPicture(pictureId, pictureEditResponseMessage, null);
	}

	/**
	 * 广播消息
	 *
	 * @param pictureId                  图片 ID
	 * @param pictureEditResponseMessage 编辑图片响应消息
	 * @param excludeSession             排除掉的 session
	 * @throws Exception e
	 */
	private void broadcastToPicture(Long pictureId, PictureEditResponseMessage pictureEditResponseMessage, WebSocketSession excludeSession) throws Exception {
		Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
		if (CollUtil.isNotEmpty(sessionSet)) {
			// 创建 ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			// 配置序列化：将 Long 类型转为 String，解决丢失精度问题
			SimpleModule module = new SimpleModule();
			module.addSerializer(Long.class, ToStringSerializer.instance);
			module.addSerializer(Long.TYPE, ToStringSerializer.instance); // 支持 long 基本类型
			objectMapper.registerModule(module);
			// 序列化为 JSON 字符串
			String message = objectMapper.writeValueAsString(pictureEditResponseMessage);
			TextMessage textMessage = new TextMessage(message);
			for (WebSocketSession session : sessionSet) {
				// 排除掉的 session 不发送
				if (excludeSession != null && excludeSession.equals(session)) {
					continue;
				}
				if (session.isOpen()) {
					session.sendMessage(textMessage);
				}
			}
		}
	}

}
