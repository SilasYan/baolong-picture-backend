package com.baolong.pictures.domain.log.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 请求日志表
 *
 * @TableName request_log
 */
@TableName(value = "request_log")
@Data
public class RequestLog implements Serializable {
	/**
	 * 主键 ID
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 操作名称
	 */
	private String opName;

	/**
	 * 操作描述
	 */
	private String opDesc;

	/**
	 * 请求路径
	 */
	private String reqPath;

	/**
	 * 请求方式（GET, POST, PUT, DELETE, PATCH）
	 */
	private String reqMethod;

	/**
	 * 全限定类名称
	 */
	private String qualifiedName;

	/**
	 * 入参
	 */
	private String inputParam;

	/**
	 * 出参
	 */
	private String outputParam;

	/**
	 * 异常信息
	 */
	private String errorMsg;

	/**
	 * 请求开始时间
	 */
	private Date reqTime;

	/**
	 * 请求响应时间
	 */
	private Date respTime;

	/**
	 * 接口耗时（单位：ms）
	 */
	private Long costTime;

	/**
	 * 请求状态
	 */
	private Integer reqStatus;

	/**
	 * 日志级别（INFO, ERROR）
	 */
	private String logLevel;

	/**
	 * 发起请求的用户 ID（0-表示无需登录）
	 */
	private Long userId;

	/**
	 * 日志来源
	 */
	private String source;

	/**
	 * 请求 IP 地址
	 */
	private String reqIp;

	/**
	 * 设备类型（PC、MOBILE）
	 */
	private String deviceType;

	/**
	 * 操作系统类型（WINDOWS, IOS, ANDROID）
	 */
	private String osType;

	/**
	 * 操作系统版本
	 */
	private String osVersion;

	/**
	 * 浏览器名称
	 */
	private String browserName;

	/**
	 * 浏览器版本
	 */
	private String browserVersion;

	/**
	 * 创建时间
	 */
	private Date createTime;

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}
