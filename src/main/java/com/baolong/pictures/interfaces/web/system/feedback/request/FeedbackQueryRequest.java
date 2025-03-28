package com.baolong.pictures.interfaces.web.system.feedback.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户反馈查询请求
 */
@Data
public class FeedbackQueryRequest implements Serializable {

	/**
	 * 用户反馈ID
	 */
	private Long feedbackId;

	/**
	 * 反馈类型（0-使用体验, 1-功能建议, 2-BUG错误, 3-其他）
	 */
	private Integer feedbackType;

	/**
	 * 联系方式（0-QQ, 1-微信, 2-邮箱）
	 */
	private Integer contactType;

	/**
	 * 联系方式内容
	 */
	private String contactInfo;

	/**
	 * 处理状态（0-待处理, 1-处理中, 2-完成, 3-拒绝）
	 */
	private Integer processStatus;

	/**
	 * 编辑时间
	 */
	private Date editTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private static final long serialVersionUID = 1L;
}
