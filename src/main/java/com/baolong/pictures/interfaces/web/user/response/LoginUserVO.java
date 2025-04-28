package com.baolong.pictures.interfaces.web.user.response;

import com.baolong.pictures.domain.system.menu.aggregate.Menu;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 登录用户响应对象
 */
@Data
public class LoginUserVO implements Serializable {

	/**
	 * 用户ID
	 */
	private Long userId;

	/**
	 * Token
	 */
	private String token;

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

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 顶部菜单列表
	 */
	private List<Menu> topMenus;

	/**
	 * 左侧菜单列表
	 */
	private List<Menu> leftMenus;

	/**
	 * 其他菜单列表
	 */
	private List<Menu> otherMenus;

	private static final long serialVersionUID = 1L;
}
