package com.baolong.pictures.application.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.application.service.UserApplicationService;
import com.baolong.pictures.domain.menu.entity.Menu;
import com.baolong.pictures.domain.menu.enums.MenuPositionEnum;
import com.baolong.pictures.domain.menu.service.MenuDomainService;
import com.baolong.pictures.domain.menu.entity.RoleMenu;
import com.baolong.pictures.domain.menu.service.RoleMenuDomainService;
import com.baolong.pictures.domain.user.entity.User;
import com.baolong.pictures.domain.user.service.UserDomainService;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.manager.redis.RedisCache;
import com.baolong.pictures.interfaces.assembler.UserAssembler;
import com.baolong.pictures.interfaces.dto.user.UserAddRequest;
import com.baolong.pictures.interfaces.dto.user.UserEditRequest;
import com.baolong.pictures.interfaces.dto.user.UserLoginRequest;
import com.baolong.pictures.interfaces.dto.user.UserQueryRequest;
import com.baolong.pictures.interfaces.dto.user.UserRegisterRequest;
import com.baolong.pictures.interfaces.dto.user.UserUpdateRequest;
import com.baolong.pictures.interfaces.vo.user.LoginUserVO;
import com.baolong.pictures.interfaces.vo.user.UserDetailVO;
import com.baolong.pictures.interfaces.vo.user.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户应用服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

	private final UserDomainService userDomainService;
	private final RoleMenuDomainService roleMenuDomainService;
	private final MenuDomainService menuDomainService;

	@Resource
	private RedisCache redisCache;

	// region 其他方法

	/**
	 * 获取查询条件对象
	 *
	 * @param userQueryRequest 用户查询请求
	 * @return 查询条件对象
	 */
	@Override
	public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
		ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
		return userDomainService.getQueryWrapper(userQueryRequest);
	}

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param userQueryRequest 用户查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@Override
	public LambdaQueryWrapper<User> getLambdaQueryWrapper(UserQueryRequest userQueryRequest) {
		ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
		return userDomainService.getLambdaQueryWrapper(userQueryRequest);
	}

	// endregion 其他方法

	// region 登录注册

	/**
	 * 发送邮箱验证码
	 *
	 * @param userRegisterRequest 用户注册请求
	 * @return 验证码 key
	 */
	@Override
	public String sendEmailCode(UserRegisterRequest userRegisterRequest) {
		User.validUserEmail(userRegisterRequest.getUserEmail());
		return userDomainService.sendEmailCode(userRegisterRequest.getUserEmail());
	}

	/**
	 * 用户注册
	 *
	 * @param userRegisterRequest 用户注册请求
	 * @return 用户ID
	 */
	@Override
	public Long userRegister(UserRegisterRequest userRegisterRequest) {
		String userEmail = userRegisterRequest.getUserEmail();
		String codeKey = userRegisterRequest.getCodeKey();
		String codeValue = userRegisterRequest.getCodeValue();
		User.validUserRegister(userEmail, codeKey, codeValue);
		return userDomainService.userRegister(userEmail, codeKey, codeValue);
	}

	/**
	 * 用户登录
	 *
	 * @param userLoginRequest 用户登录请求
	 * @return 登录用户信息
	 */
	@Override
	public LoginUserVO userLogin(UserLoginRequest userLoginRequest) {
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		String captchaKey = userLoginRequest.getCaptchaKey();
		String captchaCode = userLoginRequest.getCaptchaCode();
		User.validUserLogin(userAccount, userPassword, captchaKey, captchaCode);
		User user = userDomainService.userLogin(userAccount, userPassword, captchaKey, captchaCode);
		LoginUserVO loginUserVO = UserAssembler.toLoginUserVO(user);
		// 查询当前用户的菜单
		List<Long> menuIds = roleMenuDomainService.getBaseMapper().selectObjs(new LambdaQueryWrapper<RoleMenu>()
				.select(RoleMenu::getMenuId)
				.eq(RoleMenu::getRoleKey, loginUserVO.getUserRole())
		);
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
			loginUserVO.setTopMenus(topMenus);
			loginUserVO.setLeftMenus(leftMenus);
			loginUserVO.setOtherMenus(otherMenus);
		}
		return loginUserVO;
	}

	/**
	 * 用户退出（退出登录）
	 *
	 * @return 是否成功
	 */
	@Override
	public Boolean userLogout() {
		return userDomainService.userLogout();
	}

	// endregion 登录注册

	// region 增删改相关

	/**
	 * 新增用户
	 *
	 * @param userAddRequest 用户新增请求
	 * @return 是否成功
	 */
	@Override
	public Boolean addUser(UserAddRequest userAddRequest) {
		User user = UserAssembler.toUserEntity(userAddRequest);
		// 查询是否存在
		Boolean existed = userDomainService.existUserByEmail(userAddRequest.getUserEmail());
		ThrowUtils.throwIf(existed, ErrorCode.NOT_FOUND_ERROR, "当前邮箱绑定用户");
		return userDomainService.addUser(user);
	}

	/**
	 * 删除用户
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	@Override
	public Boolean deleteUser(DeleteRequest deleteRequest) {
		// 查询是否存在
		Boolean existed = userDomainService.existUserById(deleteRequest.getId());
		ThrowUtils.throwIf(!existed, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		return userDomainService.deleteUser(deleteRequest.getId());
	}

	/**
	 * 更新用户
	 *
	 * @param userUpdateRequest 用户修改请求
	 * @return 是否成功
	 */
	@Override
	public Boolean updateUser(UserUpdateRequest userUpdateRequest) {
		// 查询是否存在
		Boolean existed = userDomainService.existUserById(userUpdateRequest.getId());
		ThrowUtils.throwIf(!existed, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		User user = UserAssembler.toUserEntity(userUpdateRequest);
		return userDomainService.updateUser(user);
	}

	/**
	 * 编辑用户
	 *
	 * @param userEditRequest 用户编辑请求
	 * @return 是否成功
	 */
	@Override
	public Boolean editUser(UserEditRequest userEditRequest) {
		// 查询是否存在
		Boolean existed = userDomainService.existUserById(userEditRequest.getId());
		ThrowUtils.throwIf(!existed, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		User user = UserAssembler.toUserEntity(userEditRequest);
		return userDomainService.editUser(user);
	}

	/**
	 * 上传头像
	 *
	 * @param avatarFile 头像文件
	 * @return 头像地址
	 */
	@Override
	public String uploadAvatar(MultipartFile avatarFile) {
		User loginUser = this.getLoginUser();
		Long userId = loginUser.getId();
		String avatarUrl = userDomainService.uploadAvatar(avatarFile, userId);
		if (StrUtil.isEmpty(avatarUrl)) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "头像上传失败");
		}
		// 更新用户信息
		User user = new User();
		user.setId(userId);
		user.setUserAvatar(avatarUrl);
		Boolean flag = userDomainService.editUser(user);
		if (!flag) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "头像更新失败");
		}
		return avatarUrl;
	}

	/**
	 * 重置用户密码
	 *
	 * @param userUpdateRequest 用户更新请求
	 * @return 重置后的密码
	 */
	@Override
	public String resetPassword(UserUpdateRequest userUpdateRequest) {
		return userDomainService.resetPassword(userUpdateRequest.getId());
	}

	/**
	 * 禁用用户
	 *
	 * @param userUpdateRequest 用户更新请求
	 * @return 是否成功
	 */
	@Override
	public Boolean disabledUser(UserUpdateRequest userUpdateRequest) {
		// 查询是否存在
		Boolean existed = userDomainService.existUserById(userUpdateRequest.getId());
		ThrowUtils.throwIf(!existed, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		return userDomainService.disabledUser(userUpdateRequest.getId(), userUpdateRequest.getIsDisabled());
	}

	// /**
	//  * 用户兑换会员
	//  *
	//  * @param userVipExchangeRequest 用户兑换会员请求
	//  * @return 是否成功
	//  */
	// @Override
	// public Boolean userExchangeVip(UserVipExchangeRequest userVipExchangeRequest) {
	// 	String vipCode = userVipExchangeRequest.getVipCode();
	// 	ThrowUtils.throwIf(StrUtil.isNotEmpty(vipCode), ErrorCode.PARAMS_ERROR);
	// 	// return userDomainService.userExchangeVip(vipCode);
	// 	return true;
	// }

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 获取登录用户信息
	 *
	 * @return 登录用户信息
	 */
	@Override
	public User getLoginUser() {
		return userDomainService.getLoginUser();
	}

	/**
	 * 获取登录用户详情
	 *
	 * @return 登录用户详情
	 */
	@Override
	public LoginUserVO getLoginUserDetail() {
		User user = userDomainService.getLoginUser();
		// 获取登录信息
		LoginUserVO loginUserVO = UserAssembler.toLoginUserVO(user);
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
		System.out.println(JSONUtil.parse(tokenInfo));
		System.out.println(tokenInfo.getTokenValue());
		loginUserVO.setToken(tokenInfo.getTokenValue());
		return loginUserVO;
	}

	/**
	 * 根据用户 ID 获取用户信息
	 *
	 * @param userId 用户 ID
	 * @return 用户信息
	 */
	@Override
	public User getUserInfoById(Long userId) {
		return userDomainService.getUserById(userId);
	}

	/**
	 * 根据用户 ID 获取用户详情
	 *
	 * @param userId 用户 ID
	 * @return 用户详情
	 */
	@Override
	public UserDetailVO getUserDetailById(Long userId) {
		User user = userDomainService.getUserById(userId);
		return UserAssembler.toUserDetailVO(user);
	}

	/**
	 * 获取用户管理分页列表
	 *
	 * @param userQueryRequest 用户查询请求
	 * @return 用户管理分页列表
	 */
	@Override
	public PageVO<UserVO> getUserPageListAsManage(UserQueryRequest userQueryRequest) {
		Page<User> userPage = userDomainService.getUserPageListAsManage(
				userQueryRequest.getPage(User.class)
				, this.getLambdaQueryWrapper(userQueryRequest)
		);
		List<UserVO> userVOS = userPage.getRecords().stream()
				.map(UserAssembler::toUserVO)
				.collect(Collectors.toList());
		return new PageVO<>(userPage.getCurrent()
				, userPage.getSize()
				, userPage.getTotal()
				, userPage.getPages()
				, userVOS
		);
	}

	/**
	 * 根据用户 ID 集合获取用户列表
	 *
	 * @param userIds 用户 ID 集合
	 * @return 用户列表
	 */
	@Override
	public List<User> getUserListByIds(Set<Long> userIds) {
		return userDomainService.getUserListByIds(userIds);
	}

	// endregion 查询相关

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
	// @Override
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
	// @Override
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




