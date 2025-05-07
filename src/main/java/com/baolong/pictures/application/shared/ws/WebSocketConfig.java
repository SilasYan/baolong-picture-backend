package com.baolong.pictures.application.shared.ws;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * WebSocket配置
 *
 * @author Silas Yan 2025-05-06 21:23
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	@Resource
	private WsHandshakeInterceptor wsHandshakeInterceptor;
	@Resource
	private WsPictureEditHandler wsPictureEditHandler;

	/**
	 * Register {@link WebSocketHandler WebSocketHandlers} including SockJS fallback options if desired.
	 *
	 * @param registry
	 */
	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(wsPictureEditHandler, "/ws/picture/edit")
				.addInterceptors(wsHandshakeInterceptor)
				.setAllowedOrigins("*");
	}
}
