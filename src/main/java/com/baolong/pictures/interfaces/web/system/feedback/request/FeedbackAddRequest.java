package com.baolong.pictures.interfaces.web.system.feedback.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户反馈新增请求
 */
@Data
public class FeedbackAddRequest implements Serializable {

	/**
	 * 反馈类型（0-使用体验, 1-功能建议, 2-BUG错误, 3-其他）
	 */
	private Integer feedbackType;

	/**
	 * 反馈内容
	 */
	private String content;

	/**
	 * 联系方式（0-QQ, 1-微信, 2-邮箱）
	 */
	private Integer contactType;

	/**
	 * 联系方式内容
	 */
	private String contactInfo;

	private static final long serialVersionUID = 1L;
}
