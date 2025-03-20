package com.baolong.pictures.application.shared.websocket;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.application.service.PictureApplicationService;
import com.baolong.pictures.application.service.SpaceApplicationService;
import com.baolong.pictures.application.service.UserApplicationService;
import com.baolong.pictures.domain.picture.entity.Picture;
import com.baolong.pictures.domain.space.entity.Space;
import com.baolong.pictures.domain.space.enums.SpaceTypeEnum;
import com.baolong.pictures.domain.user.entity.User;
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
 * WebSocket 握手拦截器
 */
@Component
@Slf4j
public class WsHandshakeInterceptor implements HandshakeInterceptor {

	@Resource
	private UserApplicationService userApplicationService;

	@Resource
	private PictureApplicationService pictureApplicationService;

	@Resource
	private SpaceApplicationService spaceApplicationService;

	// @Resource
	// private SpaceUserAuthManager spaceUserAuthManager;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) {
		if (request instanceof ServletServerHttpRequest) {
			HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
			// 获取请求参数
			String pictureId = servletRequest.getParameter("pictureId");
			if (StrUtil.isBlank(pictureId)) {
				log.error("缺少图片参数，拒绝握手");
				return false;
			}
			User loginUser = userApplicationService.getLoginUser();
			if (ObjUtil.isEmpty(loginUser)) {
				log.error("用户未登录，拒绝握手");
				return false;
			}
			// 校验用户是否有该图片的权限
			Picture tePicture = pictureApplicationService.getPictureInfoById(Long.valueOf(pictureId));
			if (tePicture == null) {
				log.error("图片不存在，拒绝握手");
				return false;
			}
			Long spaceId = tePicture.getSpaceId();
			Space space = null;
			if (spaceId != null) {
				space = spaceApplicationService.getSpaceInfoById(spaceId);
				if (space == null) {
					log.error("空间不存在，拒绝握手");
					return false;
				}
				if (space.getSpaceType() != SpaceTypeEnum.TEAM.getKey()) {
					log.info("不是团队空间，拒绝握手");
					return false;
				}
			}
			// TODO 需要重新写
			// List<String> permissionList = spaceUserAuthManager.getPermissionList(teSpace, loginTUser);
			// if (!permissionList.contains(SpaceUserPermissionConstant.PICTURE_EDIT)) {
			// 	log.error("没有图片编辑权限，拒绝握手");
			// 	return false;
			// }
			// 设置 attributes
			attributes.put("user", loginUser);
			attributes.put("userId", loginUser.getId());
			attributes.put("pictureId", Long.valueOf(pictureId)); // 记得转换为 Long 类型
		}
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
	}
}
