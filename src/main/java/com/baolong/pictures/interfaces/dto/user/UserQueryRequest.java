package com.baolong.pictures.interfaces.dto.user;

import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {

	/**
	 * 用户 ID
	 */
	private Long id;

	/**
	 * 账号
	 */
	private String userAccount;

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

	private static final long serialVersionUID = 1L;
}
