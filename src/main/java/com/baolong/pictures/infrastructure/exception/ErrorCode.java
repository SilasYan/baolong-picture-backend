package com.baolong.pictures.infrastructure.exception;

import lombok.Getter;

/**
 * 错误码枚举类
 */
@Getter
public enum ErrorCode {

	SUCCESS(0, "ok"),
	FAILED(444, "err"),
	PARAMS_ERROR(400, "请求参数错误"),
	FORBIDDEN_ERROR(400, "禁止访问"),
	NOT_LOGIN_ERROR(401, "未登录"),
	NO_AUTH_ERROR(403, "无权限"),
	DATA_ERROR(404, "数据错误"),
	NOT_FOUND_ERROR(404, "数据不存在"),
	SYSTEM_ERROR(500, "系统内部异常"),
	REQUEST_LIMIT_ERROR(505, "访问过于频繁，请稍候再试"),
	OPERATION_ERROR(501, "操作失败");

	/**
	 * 状态码
	 */
	private final int code;

	/**
	 * 信息
	 */
	private final String message;

	ErrorCode(int code, String message) {
		this.code = code;
		this.message = message;
	}

}
