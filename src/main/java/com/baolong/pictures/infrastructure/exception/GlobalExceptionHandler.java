package com.baolong.pictures.infrastructure.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(NotLoginException.class)
	public BaseResponse<?> notLoginException(NotLoginException e) {
		log.error("NotLoginException", e);
		return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR, e.getMessage());
	}

	@ExceptionHandler(NotPermissionException.class)
	public BaseResponse<?> notPermissionExceptionHandler(NotPermissionException e) {
		log.error("NotPermissionException", e);
		return ResultUtils.error(ErrorCode.NO_AUTH_ERROR, e.getMessage());
	}

	@ExceptionHandler(BusinessException.class)
	public BaseResponse<?> businessExceptionHandler(BusinessException e) {
		log.error("BusinessException", e);
		return ResultUtils.error(e.getCode(), e.getMessage());
	}

	@ExceptionHandler(RuntimeException.class)
	public BaseResponse<?> runtimeExceptionHandler(RuntimeException e) {
		log.error("RuntimeException", e);
		return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
	}
}

