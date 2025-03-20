package com.baolong.pictures.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.application.service.impl.UserApplicationService;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.interfaces.web.user.assembler.UserAssembler;
import com.baolong.pictures.interfaces.web.user.request.UserEditRequest;
import com.baolong.pictures.interfaces.web.user.request.UserLoginRequest;
import com.baolong.pictures.interfaces.web.user.request.UserQueryRequest;
import com.baolong.pictures.interfaces.web.user.request.UserRegisterRequest;
import com.baolong.pictures.interfaces.web.user.request.UserUpdateRequest;
import com.baolong.pictures.interfaces.web.user.response.LoginUserVO;
import com.baolong.pictures.interfaces.web.user.response.UserDetailVO;
import com.baolong.pictures.interfaces.web.user.response.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户表 (user) - 接口
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserApplicationService userApplicationService;

	// region 用户相关

	/**
	 * 发送邮箱验证码
	 *
	 * @param userRegisterRequest 用户注册请求
	 * @return 验证码 key
	 */
	@PostMapping("/send/email/code")
	public BaseResponse<String> sendEmailCode(@RequestBody UserRegisterRequest userRegisterRequest) {
		ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
		String userEmail = userRegisterRequest.getUserEmail();
		if (StrUtil.isEmpty(userEmail)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱不能为空");
		}
		// 校验邮箱格式
		if (StrUtil.isEmpty(userEmail) || !ReUtil.isMatch(RegexPool.EMAIL, userEmail)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
		}
		return ResultUtils.success(userApplicationService.sendEmailCode(userEmail));
	}

	/**
	 * 用户注册
	 *
	 * @param userRegisterRequest 用户注册请求
	 * @return 是否成功
	 */
	@PostMapping("/register")
	public BaseResponse<Boolean> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
		String userEmail = userRegisterRequest.getUserEmail();
		String codeKey = userRegisterRequest.getCodeKey();
		String codeValue = userRegisterRequest.getCodeValue();
		if (StrUtil.hasBlank(userEmail, codeKey, codeValue)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		// 校验邮箱格式
		if (StrUtil.isEmpty(userEmail) || !ReUtil.isMatch(RegexPool.EMAIL, userEmail)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
		}
		return ResultUtils.success(userApplicationService.userRegister(userEmail, codeKey, codeValue));
	}

	/**
	 * 用户登录
	 *
	 * @return 登录用户信息
	 */
	@PostMapping("/login")
	public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
		ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
		String userAccount = userLoginRequest.getUserAccount();
		String userPassword = userLoginRequest.getUserPassword();
		String captchaKey = userLoginRequest.getCaptchaKey();
		String captchaCode = userLoginRequest.getCaptchaCode();
		if (StrUtil.hasBlank(userAccount, userPassword, captchaKey, captchaCode)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
		}
		if (userAccount.length() < 4 || userPassword.length() < 8) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或密码输入错误");
		}
		User user = userApplicationService.userLogin(userAccount, userPassword, captchaKey, captchaCode);
		return ResultUtils.success(UserAssembler.toLoginUserVO(user));
	}

	/**
	 * 用户退出
	 *
	 * @return 是否成功
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout() {
		return ResultUtils.success(userApplicationService.userLogout());
	}

	/**
	 * 编辑用户
	 *
	 * @return 是否成功
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editUser(@RequestBody UserEditRequest userEditRequest) {
		ThrowUtils.throwIf(userEditRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(userEditRequest.getId()), ErrorCode.PARAMS_ERROR);
		if (userEditRequest.getUserProfile().length() > 500) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户简介过长");
		}
		return ResultUtils.success(userApplicationService.editUser(UserAssembler.toUserEntity(userEditRequest)));
	}

	/**
	 * 上传头像
	 *
	 * @param avatarFile 头像文件
	 * @return 头像地址
	 */
	@PostMapping("/uploadAvatar")
	public BaseResponse<String> uploadAvatar(@RequestParam("file") MultipartFile avatarFile) {
		ThrowUtils.throwIf(avatarFile == null, ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.uploadAvatar(avatarFile));
	}

	/**
	 * 获取登录用户详情
	 *
	 * @return 登录用户详情
	 */
	@GetMapping("/loginDetail")
	public BaseResponse<LoginUserVO> getLoginUserDetail() {
		if (!StpUtil.isLogin()) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
		}
		User user = userApplicationService.getLoginUserDetail();
		LoginUserVO loginUserVO = UserAssembler.toLoginUserVO(user);
		return ResultUtils.success(loginUserVO);
	}

	/**
	 * 根据用户ID获取用户信息详情
	 *
	 * @param id 用户ID
	 * @return 用户信息详情
	 */
	@GetMapping("/detail")
	public BaseResponse<UserDetailVO> getUserDetailById(Long id) {
		if (!StpUtil.isLogin()) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
		}
		ThrowUtils.throwIf(ObjectUtil.isEmpty(id), ErrorCode.PARAMS_ERROR);
		User user = userApplicationService.getUserDetailById(id);
		return ResultUtils.success(UserAssembler.toUserDetailVO(user));
	}

	// endregion 用户相关

	// region 管理员相关

	/**
	 * 删除用户
	 *
	 * @param deleteRequest 删除用户请求
	 * @return 是否成功
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
		Long id = deleteRequest.getId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(id), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.deleteUser(id));
	}

	/**
	 * 更新用户
	 *
	 * @param userUpdateRequest 用户更新请求
	 * @return 是否成功
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
		ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(userUpdateRequest.getId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.updateUser(UserAssembler.toUserEntity(userUpdateRequest)));
	}

	/**
	 * 重置用户密码
	 *
	 * @param userUpdateRequest 用户更新请求
	 * @return 新密码
	 */
	@PostMapping("/resetPassword")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<String> resetPassword(@RequestBody UserUpdateRequest userUpdateRequest) {
		ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		Long id = userUpdateRequest.getId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(id), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.resetPassword(id));
	}

	/**
	 * 禁用用户
	 *
	 * @param userUpdateRequest 用户更新请求
	 * @return 是否成功
	 */
	@PostMapping("/disabledUser")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> disabledUser(@RequestBody UserUpdateRequest userUpdateRequest) {
		ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		Long id = userUpdateRequest.getId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(id), ErrorCode.PARAMS_ERROR);
		Integer isDisabled = userUpdateRequest.getIsDisabled();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(isDisabled), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.disabledUser(id, isDisabled));
	}

	/**
	 * 获取用户管理分页列表
	 *
	 * @param userQueryRequest 用户查询请求
	 * @return 用户管理分页列表
	 */
	@PostMapping("/manage/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<UserVO>> getUserPageListAsManage(@RequestBody UserQueryRequest userQueryRequest) {
		PageVO<User> userPage = userApplicationService.getUserPageListAsManage(UserAssembler.toUserEntity(userQueryRequest));
		return ResultUtils.success(UserAssembler.toUserVOPage(userPage));
	}

	// endregion 管理员相关

	// /**
	//  * 用户兑换会员
	//  */
	// @PostMapping("/exchange/vip")
	// public BaseResponse<Boolean> userExchangeVip(@RequestBody UserVipExchangeRequest userVipExchangeRequest) {
	// 	ThrowUtils.throwIf(userVipExchangeRequest == null, ErrorCode.PARAMS_ERROR);
	// 	// String vipCode = vipExchangeRequest.getVipCode();
	// 	// User loginUser = userApplicationService.getLoginUser(httpServletRequest);
	// 	// // 调用 service 层的方法进行会员兑换
	// 	// boolean result = userApplicationService.exchangeVip(loginUser, vipCode);
	// 	return ResultUtils.success(userApplicationService.userExchangeVip(userVipExchangeRequest));
	// }

	//
	// /**
	//  * 兑换会员
	//  */
	// @PostMapping("/exchange/vip")
	// public BaseResponse<Boolean> exchangeVip(@RequestBody UserVipExchangeRequest vipExchangeRequest,
	// 										 HttpServletRequest httpServletRequest) {
	// 	ThrowUtils.throwIf(vipExchangeRequest == null, ErrorCode.PARAMS_ERROR);
	// 	String vipCode = vipExchangeRequest.getVipCode();
	// 	User loginUser = userApplicationService.getLoginUser(httpServletRequest);
	// 	// 调用 service 层的方法进行会员兑换
	// 	boolean result = userApplicationService.exchangeVip(loginUser, vipCode);
	// 	return ResultUtils.success(result);
	// }
	//
	// /**
	//  * 上传头像
	//  */
	// @PostMapping("/uploadAvatar")
	// public BaseResponse<String> uploadAvatar(@RequestPart("file") MultipartFile avatarFile, HttpServletRequest request) {
	// 	User loginUser = userApplicationService.getLoginUser(request);
	// 	return ResultUtils.success(userApplicationService.uploadAvatar(avatarFile, request, loginUser));
	// }
}
