package com.baolong.pictures.infrastructure.persistence.user.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @TableName user
 */
@TableName(value = "user")
@Data
public class UserDO implements Serializable {

	/**
	 * 主键ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 账号
	 */
	private String userAccount;

	/**
	 * 密码
	 */
	private String userPassword;

	/**
	 * 用户邮箱
	 */
	private String userEmail;

	/**
	 * 用户手机号
	 */
	private String userPhone;

	/**
	 * 用户昵称
	 */
	private String userName;

	/**
	 * 用户头像
	 */
	private String userAvatar;

	/**
	 * 用户简介
	 */
	private String userProfile;

	/**
	 * 用户角色（USER-普通用户, ADMIN-管理员）
	 */
	private String userRole;

	/**
	 * 出生日期
	 */
	private Date birthday;

	/**
	 * 会员编号
	 */
	private Long vipNumber;

	/**
	 * 会员过期时间
	 */
	private Date vipExpireTime;

	/**
	 * 会员兑换码
	 */
	private String vipCode;

	/**
	 * 会员标识（vip 表的类型字段）
	 */
	private String vipSign;

	/**
	 * 分享码
	 */
	private String shareCode;

	/**
	 * 邀请用户 ID
	 */
	private Long inviteUserId;

	/**
	 * 是否禁用（0-正常, 1-禁用）
	 */
	private Integer isDisabled;

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
