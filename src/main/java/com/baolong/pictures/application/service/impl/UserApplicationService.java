package com.baolong.pictures.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baolong.pictures.domain.menu.entity.Menu;
import com.baolong.pictures.domain.menu.entity.RoleMenu;
import com.baolong.pictures.domain.menu.enums.MenuPositionEnum;
import com.baolong.pictures.domain.menu.service.MenuDomainService;
import com.baolong.pictures.domain.menu.service.RoleMenuDomainService;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.domain.user.service.impl.UserDomainService;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户表 (user) - 应用服务
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

	private final UserDomainService userDomainService;
	private final RoleMenuDomainService roleMenuDomainService;
	private final MenuDomainService menuDomainService;

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
	 * @return 是否成功
	 */
	public boolean userRegister(String userEmail, String codeKey, String codeValue) {
		return userDomainService.userRegister(userEmail, codeKey, codeValue);
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
		// 查询当前用户的菜单
		List<Long> menuIds = roleMenuDomainService.getBaseMapper().selectObjs(new LambdaQueryWrapper<RoleMenu>()
				.select(RoleMenu::getMenuId)
				.eq(RoleMenu::getRoleKey, user.getUserRole())
		);
		// TODO 下面这些需要转入到 菜单 领域层中处理
		if (CollUtil.isNotEmpty(menuIds)) {
			List<Menu> menus = menuDomainService.listByIds(menuIds);
			List<String> topMenus = menus.stream()
					.filter(menu -> menu.getMenuPosition().equals(MenuPositionEnum.TOP.getKey()))
					.map(Menu::getMenuPath)
					.collect(Collectors.toList());
			List<String> leftMenus = menus.stream()
					.filter(menu -> menu.getMenuPosition().equals(MenuPositionEnum.LEFT.getKey()))
					.map(Menu::getMenuPath)
					.collect(Collectors.toList());
			List<String> otherMenus = menus.stream()
					.filter(menu -> menu.getMenuPosition().equals(MenuPositionEnum.OTHER.getKey()))
					.map(Menu::getMenuPath)
					.collect(Collectors.toList());
			user.setTopMenus(topMenus);
			user.setLeftMenus(leftMenus);
			user.setOtherMenus(otherMenus);
		}
		return user;
	}

	/**
	 * 用户退出
	 *
	 * @return 是否成功
	 */
	public boolean userLogout() {
		return userDomainService.userLogout();
	}

	/**
	 * 编辑用户
	 *
	 * @param user 用户
	 * @return 是否成功
	 */
	public boolean editUser(User user) {
		return userDomainService.editUser(user);
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
	 * @return 是否成功
	 */
	public boolean deleteUser(Long userId) {
		return userDomainService.deleteUser(userId);
	}

	/**
	 * 更新用户
	 *
	 * @param user 用户
	 * @return 是否成功
	 */
	public boolean updateUser(User user) {
		return userDomainService.updateUser(user);
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
	 * @return 是否成功
	 */
	public boolean disabledUser(Long userId, Integer isDisabled) {
		return userDomainService.disabledUser(userId, isDisabled);
	}

	/**
	 * 获取登录用户详情
	 *
	 * @return 用户领域对象
	 */
	public User getLoginUserDetail() {
		return userDomainService.getUser();
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
	 * @param user 用户
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

	// /**
	//  * 根据用户 ID 集合获取用户列表
	//  *
	//  * @param userIds 用户 ID 集合
	//  * @return 用户列表
	//  */
	// public List<User> getUserListByIds(Set<Long> userIds) {
	// 	return userDomainService.getUserListByIds(userIds);
	// }

	// /**
	//  * 用户兑换会员
	//  *
	//  * @param userVipExchangeRequest 用户兑换会员请求
	//  * @return 是否成功
	//  */
	//
	// public Boolean userExchangeVip(UserVipExchangeRequest userVipExchangeRequest) {
	// 	String vipCode = userVipExchangeRequest.getVipCode();
	// 	ThrowUtils.throwIf(StrUtil.isNotEmpty(vipCode), ErrorCode.PARAMS_ERROR);
	// 	// return userDomainService.userExchangeVip(vipCode);
	// 	return true;
	// }
	// // region ------- 以下代码为用户兑换会员功能 --------
	//
	// private final ResourceLoader resourceLoader;
	//
	// // 文件读写锁（确保并发安全）
	// private final ReentrantLock fileLock = new ReentrantLock();
	//
	// /**
	//  * 会员码兑换兑换会员
	//  *
	//  * @param user    用户
	//  * @param vipCode 会员码
	//  * @return 是否兑换成功
	//  */
	// 
	// public Boolean exchangeVip(User user, String vipCode) {
	// 	// 1. 参数校验
	// 	if (user == null || StrUtil.isBlank(vipCode)) {
	// 		throw new BusinessException(ErrorCode.PARAMS_ERROR);
	// 	}
	// 	// 2. 读取并校验兑换码
	// 	UserVipCode targetCode = validateAndMarkVipCode(vipCode);
	// 	// 3. 更新用户信息
	// 	updateUserVipInfo(user, targetCode.getCode());
	// 	return true;
	// }
	//
	// /**
	//  * 校验兑换码并标记为已使用
	//  */
	// private UserVipCode validateAndMarkVipCode(String vipCode) {
	// 	fileLock.lock(); // 加锁保证文件操作原子性
	// 	try {
	// 		// 读取 JSON 文件
	// 		JSONArray jsonArray = readVipCodeFile();
	//
	// 		// 查找匹配的未使用兑换码
	// 		List<UserVipCode> codes = JSONUtil.toList(jsonArray, UserVipCode.class);
	// 		UserVipCode target = codes.stream()
	// 				.filter(code -> code.getCode().equals(vipCode) && !code.isHasUsed())
	// 				.findFirst()
	// 				.orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "无效的兑换码"));
	//
	// 		// 标记为已使用
	// 		target.setHasUsed(true);
	//
	// 		// 写回文件
	// 		writeVipCodeFile(JSONUtil.parseArray(codes));
	// 		return target;
	// 	} finally {
	// 		fileLock.unlock();
	// 	}
	// }
	//
	// /**
	//  * 读取兑换码文件
	//  */
	// private JSONArray readVipCodeFile() {
	// 	try {
	// 		// Resource resource = resourceLoader.getResource("classpath:biz/userVipCode.json");
	// 		Resource resource = resourceLoader.getResource("/userVipCode.json");
	// 		String content = FileUtil.readString(resource.getFile(), StandardCharsets.UTF_8);
	// 		return JSONUtil.parseArray(content);
	// 	} catch (IOException e) {
	// 		log.error("读取兑换码文件失败", e);
	// 		throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统繁忙");
	// 	}
	// }
	//
	// /**
	//  * 写入兑换码文件
	//  */
	// private void writeVipCodeFile(JSONArray jsonArray) {
	// 	try {
	// 		// Resource resource = resourceLoader.getResource("classpath:biz/userVipCode.json");
	// 		Resource resource = resourceLoader.getResource("/userVipCode.json");
	// 		FileUtil.writeString(jsonArray.toStringPretty(), resource.getFile(), StandardCharsets.UTF_8);
	// 	} catch (IOException e) {
	// 		log.error("更新兑换码文件失败", e);
	// 		throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统繁忙");
	// 	}
	// }
	//
	// /**
	//  * 更新用户会员信息
	//  */
	// private void updateUserVipInfo(User user, String usedVipCode) {
	// 	User currentUser = this.lambdaQuery().eq(User::getId, user.getId()).one();
	// 	// 如果当前用户已经有了会员, 并且会员未过期, 则在该日期基础上加上365天, 否则为当前时间加365天
	// 	Date expireTime;
	// 	if (currentUser.getVipExpireTime() != null && currentUser.getVipExpireTime().after(new Date())) {
	// 		expireTime = DateUtil.offsetDay(currentUser.getVipExpireTime(), 365); // 计算当前时间加 365 天后的时间
	// 	} else {
	// 		expireTime = DateUtil.offsetDay(new Date(), 365); // 计算当前时间加 365 天后的时间
	// 	}
	// 	// 构建更新对象
	// 	User updateUser = new User();
	// 	updateUser.setId(user.getId());
	// 	updateUser.setVipExpireTime(expireTime); // 设置过期时间
	// 	updateUser.setVipCode(usedVipCode);     // 记录使用的兑换码
	// 	updateUser.setVipSign(UserVipEnum.VIP.getValue());       // 修改用户会员角色
	// 	if (currentUser.getVipNumber() == null) {
	// 		// 查询用户表中 vipNumber 最大的那一条数据
	// 		User maxVipNumberUser = this.lambdaQuery().select(User::getVipNumber).orderByDesc(User::getVipNumber).last("limit 1").one();
	// 		if (maxVipNumberUser == null) {
	// 			updateUser.setVipNumber(10000L); // 如果没有数据，则设置会员编号为 1
	// 		} else {
	// 			updateUser.setVipNumber(maxVipNumberUser.getVipNumber() + 1); // 修改用户会员编号
	// 		}
	// 	}
	// 	// 执行更新
	// 	boolean updated = this.updateById(updateUser);
	// 	if (!updated) {
	// 		throw new BusinessException(ErrorCode.OPERATION_ERROR, "开通会员失败，操作数据库失败");
	// 	}
	// }
	//
	// // endregion ------- 以下代码为用户兑换会员功能 --------
	//
	// /**
	//  * 上传头像
	//  *
	//  * @param avatarFile 头像文件
	//  * @param request    HttpServletRequest
	//  * @param loginUser  登录的用户
	//  * @return 头像地址
	//  */
	// 
	// public String uploadAvatar(MultipartFile avatarFile, HttpServletRequest request, User loginUser) {
	// 	ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
	// 	// 上传头像, 头像统一管理
	// 	String uploadPathPrefix = String.format("avatar/%s", loginUser.getId());
	// 	PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
	// 	UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(avatarFile, uploadPathPrefix);
	// 	String originUrl = uploadPictureResult.getOriginUrl();
	// 	if (StrUtil.isBlank(originUrl)) {
	// 		throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传头像失败");
	// 	}
	// 	// 更新用户头像
	// 	User user = new User();
	// 	user.setId(loginUser.getId());
	// 	user.setUserAvatar(originUrl);
	// 	boolean updated = this.updateById(user);
	// 	if (!updated) {
	// 		throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新用户头像失败");
	// 	}
	// 	return originUrl;
	// }
}




