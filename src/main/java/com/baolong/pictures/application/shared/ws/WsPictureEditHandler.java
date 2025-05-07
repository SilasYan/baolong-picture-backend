package com.baolong.pictures.application.shared.ws;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.application.service.UserApplicationService;
import com.baolong.pictures.application.shared.ws.disruptor.DisruptorEventProducer;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureEditActionEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureEditMessageTypeEnum;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.interfaces.web.picture.request.PictureEditMessageRequest;
import com.baolong.pictures.interfaces.web.picture.response.PictureEditMessageVO;
import com.baolong.pictures.interfaces.web.user.assembler.UserAssembler;
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
 * WebSocket图片编辑处理器
 *
 * @author Silas Yan 2025-05-06 21:26
 */
@Component
public class WsPictureEditHandler extends TextWebSocketHandler {

	@Resource
	private DisruptorEventProducer disruptorEventProducer;
	@Resource
	private UserApplicationService userApplicationService;

	/**
	 * 图片的编辑状态，key: 图片ID, value: 正在编辑的用户ID
	 */
	private final Map<Long, Long> pictureEditingUsers = new ConcurrentHashMap<>();

	/**
	 * 所有连接的会话，key: 图片ID, value: 用户会话集合
	 */
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
		PictureEditMessageVO pictureEditMessageVO = new PictureEditMessageVO();
		pictureEditMessageVO.setType(PictureEditMessageTypeEnum.INFO.getKey());
		pictureEditMessageVO.setMessage(String.format("用户【%s】加入编辑", user.getUserName()));
		pictureEditMessageVO.setUser(UserAssembler.toUserVO(user));

		// 获取到当前正在编辑的用户信息
		Long inUserId = pictureEditingUsers.get(pictureId);
		if (inUserId != null) {
			User inUser = userApplicationService.getUserDetailById(inUserId);
			pictureEditMessageVO.setInUser(UserAssembler.toUserVO(inUser));
		}

		// 广播给同一张图片的用户
		broadcastToPicture(pictureId, pictureEditMessageVO, null);
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
		PictureEditMessageRequest pictureEditMessageRequest = JSONUtil.toBean(message.getPayload(), PictureEditMessageRequest.class);
		// 从 Session 属性中获取公共参数
		Map<String, Object> attributes = session.getAttributes();
		User user = (User) attributes.get("user");
		Long pictureId = (Long) attributes.get("pictureId");
		// 把消息放入到生产者队列
		disruptorEventProducer.publishEvent(pictureEditMessageRequest, session, user, pictureId);
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

		PictureEditMessageVO pictureEditMessageVO = new PictureEditMessageVO();
		pictureEditMessageVO.setType(PictureEditMessageTypeEnum.INFO.getKey());
		pictureEditMessageVO.setMessage(String.format("用户【%s】退出编辑", user.getUserName()));
		pictureEditMessageVO.setUser(UserAssembler.toUserVO(user));
		broadcastToPicture(pictureId, pictureEditMessageVO, null);
	}

	/**
	 * 处理进入编辑消息
	 *
	 * @param pictureEditMessageRequest 图片编辑消息请求对象
	 * @param session                   事件会话
	 * @param user                      用户对象
	 * @param pictureId                 图片ID
	 * @throws Exception e
	 */
	public void handleEnterEditMessage(PictureEditMessageRequest pictureEditMessageRequest
			, WebSocketSession session, User user, Long pictureId) throws Exception {
		// 没有用户正在编辑该图片，才能进入编辑
		if (!pictureEditingUsers.containsKey(pictureId)) {
			// 设置当前用户为编辑用户
			pictureEditingUsers.put(pictureId, user.getUserId());
			PictureEditMessageVO pictureEditMessageVO = new PictureEditMessageVO();
			pictureEditMessageVO.setType(PictureEditMessageTypeEnum.ENTER_EDIT.getKey());
			pictureEditMessageVO.setMessage(String.format("用户【%s】开始图片编辑", user.getUserName()));
			pictureEditMessageVO.setUser(UserAssembler.toUserVO(user));
			broadcastToPicture(pictureId, pictureEditMessageVO, null);
		}
	}

	/**
	 * 处理编辑操作消息
	 *
	 * @param pictureEditMessageRequest 图片编辑消息请求对象
	 * @param session                   事件会话
	 * @param user                      用户对象
	 * @param pictureId                 图片ID
	 * @throws Exception e
	 */
	public void handleEditActionMessage(PictureEditMessageRequest pictureEditMessageRequest
			, WebSocketSession session, User user, Long pictureId) throws Exception {
		Long editingUserId = pictureEditingUsers.get(pictureId);
		String action = pictureEditMessageRequest.getAction();
		PictureEditActionEnum actionEnum = PictureEditActionEnum.of(action);
		if (actionEnum != null && editingUserId != null && editingUserId.equals(user.getUserId())) {
			PictureEditMessageVO pictureEditMessageVO = new PictureEditMessageVO();
			pictureEditMessageVO.setType(PictureEditMessageTypeEnum.EDIT_ACTION.getKey());
			pictureEditMessageVO.setMessage(String.format("用户【%s】执行【%s】操作", user.getUserName(), actionEnum.getLabel()));
			pictureEditMessageVO.setAction(action);
			pictureEditMessageVO.setUser(UserAssembler.toUserVO(user));
			// 广播给除了当前客户端之外的其他用户，否则会造成重复编辑
			broadcastToPicture(pictureId, pictureEditMessageVO, session);
		}
	}

	/**
	 * 处理退出编辑消息
	 *
	 * @param pictureEditMessageRequest 图片编辑消息请求对象
	 * @param session                   事件会话
	 * @param user                      用户对象
	 * @param pictureId                 图片ID
	 * @throws Exception e
	 */
	public void handleExitEditMessage(PictureEditMessageRequest pictureEditMessageRequest
			, WebSocketSession session, User user, Long pictureId) throws Exception {
		Long editingUserId = pictureEditingUsers.get(pictureId);
		if (editingUserId != null && editingUserId.equals(user.getUserId())) {
			// 移除当前用户的编辑状态
			pictureEditingUsers.remove(pictureId);
			// 构造响应，发送退出编辑的消息通知
			PictureEditMessageVO pictureEditMessageVO = new PictureEditMessageVO();
			pictureEditMessageVO.setType(PictureEditMessageTypeEnum.EXIT_EDIT.getKey());
			pictureEditMessageVO.setMessage(String.format("用户【%s】退出图片编辑", user.getUserName()));
			pictureEditMessageVO.setUser(UserAssembler.toUserVO(user));
			broadcastToPicture(pictureId, pictureEditMessageVO, null);
		}
	}

	/**
	 * 处理完成编辑消息
	 *
	 * @param pictureEditMessageRequest 图片编辑消息请求对象
	 * @param session                   事件会话
	 * @param user                      用户对象
	 * @param pictureId                 图片ID
	 * @throws Exception e
	 */
	public void handleCompleteEditMessage(PictureEditMessageRequest pictureEditMessageRequest
			, WebSocketSession session, User user, Long pictureId) throws Exception {
		Long editingUserId = pictureEditingUsers.get(pictureId);
		if (editingUserId != null && editingUserId.equals(user.getUserId())) {
			// 构造响应，发送完成编辑的消息通知
			PictureEditMessageVO pictureEditMessageVO = new PictureEditMessageVO();
			pictureEditMessageVO.setType(PictureEditMessageTypeEnum.COMPLETE.getKey());
			pictureEditMessageVO.setMessage(String.format("用户【%s】完成图片编辑", user.getUserName()));
			pictureEditMessageVO.setUser(UserAssembler.toUserVO(user));
			// 广播给除了当前客户端之外的其他用户，否则会造成重复编辑
			broadcastToPicture(pictureId, pictureEditMessageVO, session);
		}
	}

	/**
	 * 广播消息
	 *
	 * @param pictureId            图片ID
	 * @param pictureEditMessageVO 图片编辑消息响应对象
	 * @param excludeSession       排除掉的 session, 如果为 null 则是广播给全部
	 * @throws Exception e
	 */
	private void broadcastToPicture(Long pictureId, PictureEditMessageVO pictureEditMessageVO
			, WebSocketSession excludeSession) throws Exception {
		Set<WebSocketSession> sessionSet = pictureSessions.get(pictureId);
		if (CollUtil.isNotEmpty(sessionSet)) {
			// 创建 ObjectMapper
			ObjectMapper objectMapper = new ObjectMapper();
			// 配置序列化：将 Long 类型转为 String，解决丢失精度问题
			SimpleModule module = new SimpleModule();
			module.addSerializer(Long.class, ToStringSerializer.instance);
			// 支持 long 基本类型
			module.addSerializer(Long.TYPE, ToStringSerializer.instance);
			objectMapper.registerModule(module);
			// 序列化为 JSON 字符串
			String message = objectMapper.writeValueAsString(pictureEditMessageVO);
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
