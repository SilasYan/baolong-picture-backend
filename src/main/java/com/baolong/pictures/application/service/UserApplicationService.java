package com.baolong.pictures.application.service;

import cn.hutool.core.collection.CollUtil;
import com.baolong.pictures.domain.menu.entity.Menu;
import com.baolong.pictures.domain.menu.entity.enums.MenuPositionEnum;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.domain.user.service.UserDomainService;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户表 (user) - 应用服务
 *
 * @author Baolong 2025年03月09 21:06
 * @version 1.0
 * @since 1.8
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {
	// 用户领域服务相关
	private final UserDomainService userDomainService;
	// 其他应用服务相关
	private final MenuApplicationService menuApplicationService;

	/**
	 * 发送邮箱验证码
	 *
	 * @param userEmail 用户邮箱
	 * @return 验证码 key
	 */
	public String sendEmailCode(String userEmail) {
		return userDomainService.sendEmailCode(userEmail);
	}

	/**
	 * 用户注册
	 *
	 * @param userEmail 用户邮箱
	 * @param codeKey   验证码 key
	 * @param codeValue 验证码 value
	 */
	public void userRegister(String userEmail, String codeKey, String codeValue) {
		userDomainService.userRegister(userEmail, codeKey, codeValue);
	}

	/**
	 * 用户登录
	 *
	 * @param userAccount  用户账户
	 * @param userPassword 用户密码
	 * @param captchaKey   图形验证码 key
	 * @param captchaCode  图形验证码 验证码
	 * @return 用户信息
	 */
	public User userLogin(String userAccount, String userPassword, String captchaKey, String captchaCode) {
		User user = userDomainService.userLogin(userAccount, userPassword, captchaKey, captchaCode);
		// 查询登录当前用户的菜单
		List<Menu> menuList = menuApplicationService.getMenuListByUserRole(user.getUserRole());
		if (CollUtil.isNotEmpty(menuList)) {
			user.setTopMenus(menuList.stream()
					.filter(menu -> menu.getMenuPosition().equals(MenuPositionEnum.TOP.getKey()))
					.map(Menu::getMenuPath).collect(Collectors.toList()));
			user.setLeftMenus(menuList.stream()
					.filter(menu -> menu.getMenuPosition().equals(MenuPositionEnum.LEFT.getKey()))
					.map(Menu::getMenuPath).collect(Collectors.toList()));
			user.setOtherMenus(menuList.stream()
					.filter(menu -> menu.getMenuPosition().equals(MenuPositionEnum.OTHER.getKey()))
					.map(Menu::getMenuPath).collect(Collectors.toList()));
		}
		return user;
	}

	/**
	 * 用户退出
	 */
	public void userLogout() {
		userDomainService.userLogout();
	}

	/**
	 * 编辑用户
	 *
	 * @param user 用户领域对象
	 */
	public void editUser(User user) {
		userDomainService.editUser(user);
	}

	/**
	 * 上传头像
	 *
	 * @param avatarFile 头像文件
	 * @return 头像地址
	 */
	public String uploadAvatar(MultipartFile avatarFile) {
		return userDomainService.uploadAvatar(avatarFile);
	}

	/**
	 * 删除用户
	 *
	 * @param userId 用户ID
	 */
	public void deleteUser(Long userId) {
		userDomainService.deleteUser(userId);
	}

	/**
	 * 更新用户
	 *
	 * @param user 用户领域对象
	 */
	public void updateUser(User user) {
		userDomainService.updateUser(user);
	}

	/**
	 * 重置用户密码
	 *
	 * @param userId 用户ID
	 * @return 新密码
	 */
	public String resetPassword(Long userId) {
		return userDomainService.resetPassword(userId);
	}

	/**
	 * 禁用用户
	 *
	 * @param userId     用户 ID
	 * @param isDisabled 是否禁用
	 */
	public void disabledUser(Long userId, Integer isDisabled) {
		userDomainService.disabledUser(userId, isDisabled);
	}

	/**
	 * 获取登录用户详情
	 *
	 * @return 用户领域对象
	 */
	public User getLoginUserDetail() {
		return userDomainService.getLoginUser();
	}

	/**
	 * 根据用户ID获取用户详情
	 *
	 * @param userId 用户ID
	 * @return 用户领域对象
	 */
	public User getUserDetailById(Long userId) {
		return userDomainService.getUserByUserId(userId);
	}

	/**
	 * 获取用户管理分页列表
	 *
	 * @param user 用户领域对象
	 * @return 用户管理分页列表
	 */
	public PageVO<User> getUserPageListAsManage(User user) {
		return userDomainService.getUserPageListAsManage(user);
	}

	/**
	 * 根据用户ID集合获取用户列表
	 *
	 * @param userIds 用户ID集合
	 * @return 用户列表
	 */
	public List<User> getUserListByUserIds(Set<Long> userIds) {
		return userDomainService.getUserListByUserIds(userIds);
	}
}




