package com.baolong.pictures.interfaces.dto.user;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户新增请求
 */
@Data
public class UserAddRequest implements Serializable {

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

	private static final long serialVersionUID = 1L;
}
