// package com.baolong.pictures.application.service;
//
// import com.baolong.pictures.domain.user.aggregate.User;
// import com.baolong.pictures.infrastructure.common.DeleteRequest;
// import com.baolong.pictures.infrastructure.common.page.PageVO;
// import com.baolong.pictures.interfaces.web.user.request.UserAddRequest;
// import com.baolong.pictures.interfaces.web.user.request.UserEditRequest;
// import com.baolong.pictures.interfaces.web.user.request.UserLoginRequest;
// import com.baolong.pictures.interfaces.web.user.request.UserQueryRequest;
// import com.baolong.pictures.interfaces.web.user.request.UserRegisterRequest;
// import com.baolong.pictures.interfaces.web.user.request.UserUpdateRequest;
// import com.baolong.pictures.interfaces.web.response.user.LoginUserVO;
// import com.baolong.pictures.interfaces.web.response.user.UserDetailVO;
// import com.baolong.pictures.interfaces.web.response.user.UserVO;
// import org.springframework.web.multipart.MultipartFile;
//
// import java.util.List;
// import java.util.Set;
//
// /**
//  * 用户应用服务接口
//  */
// public interface UserApplicationService {
//
// 	/**
// 	 * 发送邮箱验证码
// 	 *
// 	 * @param userEmail 用户邮箱
// 	 * @return 验证码 key
// 	 */
// 	String sendEmailCode(String userEmail);
//
// 	/**
// 	 * 用户注册
// 	 *
// 	 * @param userRegisterRequest 用户注册请求
// 	 * @return 用户ID
// 	 */
// 	Long userRegister(UserRegisterRequest userRegisterRequest);
//
// 	/**
// 	 * 用户登录
// 	 *
// 	 * @param userLoginRequest 用户登录请求
// 	 * @return 登录用户信息
// 	 */
// 	LoginUserVO userLogin(UserLoginRequest userLoginRequest);
//
// 	/**
// 	 * 用户退出（退出登录）
// 	 *
// 	 * @return 是否成功
// 	 */
// 	Boolean userLogout();
//
// 	// endregion 登录注册
//
// 	// region 增删改相关
//
// 	/**
// 	 * 新增用户
// 	 *
// 	 * @param userAddRequest 用户新增请求
// 	 * @return 是否成功
// 	 */
// 	Boolean addUser(UserAddRequest userAddRequest);
//
// 	/**
// 	 * 删除用户
// 	 *
// 	 * @param deleteRequest 删除请求
// 	 * @return 是否成功
// 	 */
// 	Boolean deleteUser(DeleteRequest deleteRequest);
//
// 	/**
// 	 * 更新用户
// 	 *
// 	 * @param userUpdateRequest 用户修改请求
// 	 * @return 是否成功
// 	 */
// 	Boolean updateUser(UserUpdateRequest userUpdateRequest);
//
// 	/**
// 	 * 编辑用户
// 	 *
// 	 * @param userEditRequest 用户编辑请求
// 	 * @return 是否成功
// 	 */
// 	Boolean editUser(UserEditRequest userEditRequest);
//
// 	/**
// 	 * 上传头像
// 	 *
// 	 * @param avatarFile 头像文件
// 	 * @return 头像地址
// 	 */
// 	String uploadAvatar(MultipartFile avatarFile);
//
// 	/**
// 	 * 重置用户密码
// 	 *
// 	 * @param userUpdateRequest 用户更新请求
// 	 * @return 重置后的密码
// 	 */
// 	String resetPassword(UserUpdateRequest userUpdateRequest);
//
// 	/**
// 	 * 禁用用户
// 	 *
// 	 * @param userUpdateRequest 用户更新请求
// 	 * @return 是否成功
// 	 */
// 	Boolean disabledUser(UserUpdateRequest userUpdateRequest);
//
// 	// endregion 增删改相关
//
// 	// region 查询相关
//
// 	/**
// 	 * 获取登录用户信息
// 	 *
// 	 * @return 登录用户信息
// 	 */
// 	User getLoginUser();
//
// 	/**
// 	 * 获取登录用户详情
// 	 *
// 	 * @return 登录用户详情
// 	 */
// 	LoginUserVO getLoginUserDetail();
//
// 	/**
// 	 * 根据用户 ID 获取用户信息
// 	 *
// 	 * @param userId 用户 ID
// 	 * @return 用户信息
// 	 */
// 	User getUserInfoById(Long userId);
//
// 	/**
// 	 * 根据用户 ID 获取用户详情
// 	 *
// 	 * @param userId 用户 ID
// 	 * @return 用户详情
// 	 */
// 	UserDetailVO getUserDetailById(Long userId);
//
// 	/**
// 	 * 获取用户管理分页列表
// 	 *
// 	 * @param userQueryRequest 用户查询请求
// 	 * @return 用户管理分页列表
// 	 */
// 	PageVO<UserVO> getUserPageListAsManage(UserQueryRequest userQueryRequest);
//
// 	/**
// 	 * 根据用户 ID 集合获取用户列表
// 	 *
// 	 * @param userIds 用户 ID 集合
// 	 * @return 用户列表
// 	 */
// 	List<User> getUserListByIds(Set<Long> userIds);
//
// 	// endregion 查询相关
//
// 	//
// 	// /**
// 	//  * 会员码兑换兑换会员
// 	//  *
// 	//  * @param user    用户
// 	//  * @param vipCode 会员码
// 	//  * @return 是否兑换成功
// 	//  */
// 	// Boolean exchangeVip(User user, String vipCode);
// 	//
// 	// /**
// 	//  * 上传头像
// 	//  *
// 	//  * @param avatarFile 头像文件
// 	//  * @param request    HttpServletRequest
// 	//  * @param loginUser  登录的用户
// 	//  * @return 头像地址
// 	//  */
// 	// String uploadAvatar(MultipartFile avatarFile, HttpServletRequest request, User loginUser);
// }
