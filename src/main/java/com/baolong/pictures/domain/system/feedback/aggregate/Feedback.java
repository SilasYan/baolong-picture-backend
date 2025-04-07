package com.baolong.pictures.domain.system.feedback.aggregate;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.system.feedback.aggregate.enums.FeedbackContactEnum;
import com.baolong.pictures.domain.system.feedback.aggregate.enums.FeedbackTypeEnum;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户反馈领域对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Feedback extends PageRequest implements Serializable {

	/**
	 * 用户反馈ID
	 */
	private Long feedbackId;

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

	/**
	 * 处理状态（0-待处理, 1-处理中, 2-完成, 3-拒绝）
	 */
	private Integer processStatus;

	/**
	 * 处理内容
	 */
	private String processContent;

	/**
	 * 是否删除（0-正常, 1-删除）
	 */
	private Integer isDelete;

	/**
	 * 编辑时间
	 */
	private Date editTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	public void checkAddRequest() {
		if (ObjectUtil.isEmpty(this.feedbackType) || !FeedbackTypeEnum.keys().contains(this.feedbackType)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "反馈类型错误");
		}
		if (StrUtil.isEmpty(this.content)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "反馈内容不能为空");
		}
		if (ObjectUtil.isEmpty(this.contactType) || !FeedbackContactEnum.keys().contains(this.contactType)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "联系方式错误");
		}
		if (StrUtil.isEmpty(this.contactInfo)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "联系方式内容不能为空");
		}
	}

	private static final long serialVersionUID = 1L;
}
