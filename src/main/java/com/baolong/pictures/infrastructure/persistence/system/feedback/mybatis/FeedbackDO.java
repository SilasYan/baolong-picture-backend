package com.baolong.pictures.infrastructure.persistence.system.feedback.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户反馈表
 *
 * @TableName feedback
 */
@TableName(value = "feedback")
@Data
public class FeedbackDO implements Serializable {
	/**
	 * 主键ID
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

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
	@TableField(value = "edit_time", fill = FieldFill.UPDATE)
	private Date editTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}
