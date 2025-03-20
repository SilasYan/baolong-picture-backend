package com.baolong.pictures.application.shared.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * WebSocket 配置, 指定路径配置和处理器
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Resource
	private PictureEditHandler pictureEditHandler;

	@Resource
	private WsHandshakeInterceptor wsHandshakeInterceptor;

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		// websocket
		registry.addHandler(pictureEditHandler, "/ws/picture/edit")
				.addInterceptors(wsHandshakeInterceptor)
				.setAllowedOrigins("*");
	}
}
