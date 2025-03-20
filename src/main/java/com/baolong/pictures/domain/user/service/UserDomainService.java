package com.baolong.pictures.domain.user.service;

import com.baolong.pictures.domain.user.entity.User;
import com.baolong.pictures.interfaces.dto.user.UserQueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * 用户表 (user) - 领域服务接口
 */
public interface UserDomainService extends IService<User> {

	// region 其他方法

	/**
	 * 获取查询条件对象
	 *
	 * @param userQueryRequest 用户查询请求
	 * @return 查询条件对象
	 */
	QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param userQueryRequest 用户查询请求
	 * @return 查询条件对象（Lambda）
	 */
	LambdaQueryWrapper<User> getLambdaQueryWrapper(UserQueryRequest userQueryRequest);

	// endregion 其他方法

	// region 登录注册

	/**
	 * 发送邮箱验证码
	 *
	 * @param userEmail 用户邮箱
	 * @return 验证码 key
	 */
	String sendEmailCode(String userEmail);

	/**
	 * 用户注册
	 *
	 * @param userEmail 用户邮箱
	 * @param codeKey   验证码 key
	 * @param codeValue 验证码 value
	 * @return 用户ID
	 */
	Long userRegister(String userEmail, String codeKey, String codeValue);

	/**
	 * 用户登录
	 *
	 * @param userAccount  用户账户
	 * @param userPassword 用户密码
	 * @param captchaKey   图形验证码 key
	 * @param captchaCode  图形验证码 验证码
	 * @return 用户信息
	 */
	User userLogin(String userAccount, String userPassword, String captchaKey, String captchaCode);

	/**
	 * 用户退出（退出登录）
	 *
	 * @return 是否成功
	 */
	Boolean userLogout();

	// endregion 登录注册

	// region 增删改相关

	/**
	 * 新增用户
	 *
	 * @param user 用户
	 * @return 是否成功
	 */
	Boolean addUser(User user);

	/**
	 * 删除用户
	 *
	 * @param userId 用户ID
	 * @return 是否成功
	 */
	Boolean deleteUser(Long userId);

	/**
	 * 更新用户
	 *
	 * @param user 用户
	 * @return 是否成功
	 */
	Boolean updateUser(User user);

	/**
	 * 编辑用户
	 *
	 * @param user 用户
	 * @return 是否成功
	 */
	Boolean editUser(User user);

	/**
	 * 上传头像
	 *
	 * @param avatarFile 头像文件
	 * @param userId     用户ID
	 * @return 头像地址
	 */
	String uploadAvatar(MultipartFile avatarFile, Long userId);

	/**
	 * 重置用户密码
	 *
	 * @param userId 用户密码
	 * @return 重置后的密码
	 */
	String resetPassword(Long userId);

	/**
	 * 禁用用户
	 *
	 * @param userId     用户 ID
	 * @param isDisabled 是否禁用
	 * @return 是否成功
	 */
	Boolean disabledUser(Long userId, Integer isDisabled);

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 获取登录用户信息
	 *
	 * @return 登录用户信息
	 */
	User getLoginUser();

	/**
	 * 根据用户 ID 获取用户信息
	 *
	 * @param userId 用户 ID
	 * @return 用户信息
	 */
	User getUserById(Long userId);

	/**
	 * 获取用户管理分页列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 用户管理分页列表
	 */
	Page<User> getUserPageListAsManage(Page<User> page, LambdaQueryWrapper<User> lambdaQueryWrapper);

	/**
	 * 根据用户 ID 判断用户是否存在
	 *
	 * @param userId 用户 ID
	 * @return 是否存在
	 */
	Boolean existUserById(Long userId);

	/**
	 * 根据用户邮箱判断用户是否存在
	 *
	 * @param userEmail 用户邮箱
	 * @return 是否存在
	 */
	Boolean existUserByEmail(String userEmail);

	/**
	 * 根据用户 ID 集合获取用户列表
	 *
	 * @param userIds 用户 ID 集合
	 * @return 用户列表
	 */
	List<User> getUserListByIds(Set<Long> userIds);

	// endregion 查询相关

	//
	// /**
	//  * 用户兑换会员（会员码兑换）
	//  *
	//  * @param user    登录的用户
	//  * @param vipCode 会员码
	//  * @return 是否兑换成功
	//  */
	// boolean exchangeVip(User user, String vipCode);
	//
	// /**
	//  * 上传头像
	//  *
	//  * @param avatarFile 头像文件
	//  * @param request    HttpServletRequest
	//  * @param loginUser  登录的用户
	//  * @return 头像地址
	//  */
	// String uploadAvatar(MultipartFile avatarFile, HttpServletRequest request, User loginUser);

}
