package com.baolong.pictures.interfaces.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baolong.pictures.application.service.UserApplicationService;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.user.constant.UserConstant;
import com.baolong.pictures.domain.user.entity.User;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.interfaces.dto.user.UserAddRequest;
import com.baolong.pictures.interfaces.dto.user.UserEditRequest;
import com.baolong.pictures.interfaces.dto.user.UserLoginRequest;
import com.baolong.pictures.interfaces.dto.user.UserQueryRequest;
import com.baolong.pictures.interfaces.dto.user.UserRegisterRequest;
import com.baolong.pictures.interfaces.dto.user.UserUpdateRequest;
import com.baolong.pictures.interfaces.vo.user.LoginUserVO;
import com.baolong.pictures.interfaces.vo.user.UserDetailVO;
import com.baolong.pictures.interfaces.vo.user.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserApplicationService userApplicationService;

	// region 登录注册

	/**
	 * 发送邮箱验证码
	 */
	@PostMapping("/send/email/code")
	public BaseResponse<String> sendEmailCode(@RequestBody UserRegisterRequest userRegisterRequest) {
		ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR, "参数为空");
		return ResultUtils.success(userApplicationService.sendEmailCode(userRegisterRequest));
	}

	/**
	 * 用户注册
	 */
	@PostMapping("/register")
	public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
		ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR, "参数为空");
		return ResultUtils.success(userApplicationService.userRegister(userRegisterRequest));
	}

	/**
	 * 用户登录
	 */
	@PostMapping("/login")
	public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest) {
		ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR, "参数为空");
		return ResultUtils.success(userApplicationService.userLogin(userLoginRequest));
	}

	/**
	 * 退出登录
	 */
	@PostMapping("/logout")
	public BaseResponse<Boolean> userLogout() {
		return ResultUtils.success(userApplicationService.userLogout());
	}

	// endregion 登录注册

	// region 增删改相关

	/**
	 * 新增用户
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> addUser(@RequestBody UserAddRequest userAddRequest) {
		ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.addUser(userAddRequest));
	}

	/**
	 * 删除用户
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(deleteRequest.getId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.deleteUser(deleteRequest));
	}

	/**
	 * 更新用户
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
		ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(userUpdateRequest.getId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.updateUser(userUpdateRequest));
	}

	/**
	 * 编辑用户
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editUser(@RequestBody UserEditRequest userEditRequest) {
		ThrowUtils.throwIf(userEditRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(userEditRequest.getId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.editUser(userEditRequest));
	}

	/**
	 * 上传头像
	 */
	@PostMapping("/uploadAvatar")
	public BaseResponse<String> uploadAvatar(@RequestParam("file") MultipartFile avatarFile) {
		ThrowUtils.throwIf(avatarFile == null, ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.uploadAvatar(avatarFile));
	}

	/**
	 * 重置用户密码
	 */
	@PostMapping("/resetPassword")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<String> resetPassword(@RequestBody UserUpdateRequest userUpdateRequest) {
		ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.resetPassword(userUpdateRequest));
	}

	/**
	 * 禁用用户
	 */
	@PostMapping("/disabledUser")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> disabledUser(@RequestBody UserUpdateRequest userUpdateRequest) {
		ThrowUtils.throwIf(userUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(userUpdateRequest.getId()), ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(userUpdateRequest.getIsDisabled()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.disabledUser(userUpdateRequest));
	}

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

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 获取登录用户详情
	 */
	@GetMapping("/loginDetail")
	public BaseResponse<LoginUserVO> getLoginUserDetail() {
		return ResultUtils.success(userApplicationService.getLoginUserDetail());
	}

	/**
	 * 根据用户 ID 获取用户信息
	 */
	@GetMapping("/info")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<User> getUserInfoById(Long id) {
		ThrowUtils.throwIf(ObjectUtil.isEmpty(id), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.getUserInfoById(id));
	}

	/**
	 * 根据用户 ID 获取用户信息详情
	 */
	@GetMapping("/detail")
	public BaseResponse<UserDetailVO> getUserDetailById(Long id) {
		ThrowUtils.throwIf(ObjectUtil.isEmpty(id), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(userApplicationService.getUserDetailById(id));
	}

	/**
	 * 获取用户管理分页列表
	 */
	@PostMapping("/manage/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<UserVO>> getUserPageListAsManage(@RequestBody UserQueryRequest userQueryRequest) {
		return ResultUtils.success(userApplicationService.getUserPageListAsManage(userQueryRequest));
	}

	// endregion 查询相关

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
