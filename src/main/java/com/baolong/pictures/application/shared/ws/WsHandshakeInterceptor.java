package com.baolong.pictures.application.shared.ws;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.application.service.PictureApplicationService;
import com.baolong.pictures.application.service.SpaceApplicationService;
import com.baolong.pictures.application.service.UserApplicationService;
import com.baolong.pictures.domain.picture.aggregate.Picture;
import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.domain.space.aggregate.enums.SpaceTypeEnum;
import com.baolong.pictures.domain.user.aggregate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * WebSocket握手拦截器
 *
 * @author Silas Yan 2025-05-06 21:03
 */
@Slf4j
@Component
public class WsHandshakeInterceptor implements HandshakeInterceptor {

	@Resource
	private UserApplicationService userApplicationService;
	@Resource
	private PictureApplicationService pictureApplicationService;
	@Resource
	private SpaceApplicationService spaceApplicationService;

	/**
	 * Invoked before the handshake is processed.
	 *
	 * @param request    the current request
	 * @param response   the current response
	 * @param wsHandler  the target WebSocket handler
	 * @param attributes the attributes from the HTTP handshake to associate with the WebSocket session; the provided
	 *                   attributes are copied, the original map is not used.
	 * @return whether to proceed with the handshake ({@code true}) or abort ({@code false})
	 */
	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		if (request instanceof ServletServerHttpRequest) {
			HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
			String pictureIdStr = servletRequest.getParameter("pictureId");
			if (StrUtil.isBlank(pictureIdStr)) {
				log.error("[WS]缺少图片ID，拒绝握手");
				return false;
			}
			User loginUser = userApplicationService.getLoginUserDetail();
			if (ObjUtil.isEmpty(loginUser)) {
				log.error("[WS]用户不存在，拒绝握手");
				return false;
			}
			Long pictureId = Long.valueOf(pictureIdStr);
			Picture picture = pictureApplicationService.getPictureDetailById(pictureId);
			if (ObjUtil.isEmpty(picture)) {
				log.error("[WS]图片不存在，拒绝握手");
				return false;
			}
			Long spaceId = picture.getSpaceId();
			Space space = spaceApplicationService.getSpaceBySpaceId(spaceId);
			if (ObjUtil.isEmpty(space)) {
				log.error("[WS]空间不存在，拒绝握手");
				return false;
			}
			if (SpaceTypeEnum.TEAM.getKey() != space.getSpaceType()) {
				log.info("[WS]不是团队空间，拒绝握手");
				return false;
			}
			// 获取当前用户是否具备权限, 这里内部已经做了判断,如果没有权限, 直接会报错的
			Long userId = loginUser.getUserId();
			try {
				spaceApplicationService.canOperateInSpace(spaceId, userId, loginUser.isAdmin());
			} catch (Exception e) {
				log.error("[WS]没有权限，拒绝握手");
				return false;
			}

			attributes.put("user", loginUser);
			attributes.put("userId", userId);
			attributes.put("pictureId", pictureId);
		}
		return true;
	}

	/**
	 * Invoked after the handshake is done. The response status and headers indicate the results of the handshake, i.e.
	 * whether it was successful or not.
	 *
	 * @param request   the current request
	 * @param response  the current response
	 * @param wsHandler the target WebSocket handler
	 * @param exception an exception raised during the handshake, or {@code null} if none
	 */
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

	}
}
