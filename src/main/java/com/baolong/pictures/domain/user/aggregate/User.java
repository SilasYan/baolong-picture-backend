package com.baolong.pictures.domain.user.aggregate;

import cn.hutool.core.util.RandomUtil;
import com.baolong.pictures.domain.user.aggregate.enums.UserRoleEnum;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户领域模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class User extends PageRequest implements Serializable {

	// region 原始属性

	/**
	 * 用户ID
	 */
	private Long userId;

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
	private Date editTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	// endregion 原始属性

	// region拓展属性

	/**
	 * Token
	 */
	private String token;

	/**
	 * 顶部菜单列表
	 */
	private List<String> topMenus;

	/**
	 * 左侧菜单列表
	 */
	private List<String> leftMenus;

	/**
	 * 其他菜单列表
	 */
	private List<String> otherMenus;

	// endregion拓展属性

	// region 领域方法

	/**
	 * 填充默认值
	 */
	public void fillDefaultValue() {
		String random = RandomUtil.randomString(6);
		this.setUserAccount("user_" + random);
		this.setUserName("用户_" + random);
		this.setUserRole(UserRoleEnum.USER.getKey());
		this.setUserPassword(getEncryptPassword("12345678"));
	}

	/**
	 * 获取加密的密码
	 *
	 * @param userPassword 用户密码
	 * @return 加密后的密码
	 */
	public static String getEncryptPassword(String userPassword) {
		final String SALT = "baolong";
		return DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
	}

	/**
	 * 是否为管理员
	 *
	 * @return 是否为管理员
	 */
	public Boolean isAdmin() {
		return UserRoleEnum.isAdmin(this.getUserRole());
	}

	// endregion 领域方法

	private static final long serialVersionUID = 1L;
}
