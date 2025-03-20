package com.baolong.pictures.domain.user.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.RegexPool;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.domain.user.aggregate.enums.UserDisabledEnum;
import com.baolong.pictures.domain.user.repository.UserRepository;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.constant.CacheKeyConstant;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.manager.message.EmailManager;
import com.baolong.pictures.infrastructure.manager.redis.RedisCache;
import com.baolong.pictures.infrastructure.manager.upload.picture.UploadPictureFile;
import com.baolong.pictures.infrastructure.manager.upload.picture.model.UploadPictureResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 用户表 (user) - 领域服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDomainService {
	private final UserRepository userRepository;

	private final EmailManager emailManager;
	private final RedisCache redisCache;
	private final UploadPictureFile uploadPictureFile;

	/**
	 * 发送邮箱验证码
	 *
	 * @param userEmail 用户邮箱
	 * @return 验证码 key
	 */
	public String sendEmailCode(String userEmail) {
		boolean existed = userRepository.existedUserByEmail(userEmail);
		if (existed) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在, 请直接登录!");
		}
		// 发送邮箱验证码
		String code = RandomUtil.randomNumbers(4);
		emailManager.sendEmailCode(userEmail, "暴龙图库 - 注册验证码", code);
		// 生成一个唯一 ID, 后面注册前端需要带过来
		String key = UUID.randomUUID().toString();
		// 存入 Redis, 5 分钟过期
		redisCache.set(String.format(CacheKeyConstant.EMAIL_CODE_KEY, key, userEmail), code, 5, TimeUnit.MINUTES);
		return key;
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
		String KEY = String.format(CacheKeyConstant.EMAIL_CODE_KEY, codeKey, userEmail);
		// 获取 Redis 中的验证码
		String code = redisCache.get(KEY);
		// 删除验证码
		redisCache.delete(KEY);
		if (StrUtil.isEmpty(code) || !code.equals(codeValue)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
		}
		boolean existed = userRepository.existedUserByEmail(userEmail);
		if (existed) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在, 请直接登录!");
		}
		// 构建参数
		User user = new User().setUserEmail(userEmail);
		// 默认值填充
		user.fillDefaultValue();
		boolean result = userRepository.addUser(user);
		if (!result) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败!");
		}
		return true;
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
		String KEY = String.format(CacheKeyConstant.CAPTCHA_CODE_KEY, captchaKey);
		// 获取 Redis 中的验证码
		String code = redisCache.get(KEY);
		// 删除验证码
		redisCache.delete(KEY);
		if (StrUtil.isEmpty(code) || !code.equals(captchaCode)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "验证码错误");
		}
		User user;
		if (ReUtil.isMatch(RegexPool.EMAIL, userAccount)) {
			// 根据 userEmail 获取用户信息
			user = userRepository.getUserByUserEmail(userAccount);
		} else {
			// 根据 userAccount 获取用户信息
			user = userRepository.getUserByUserAccount(userAccount);
		}
		if (user == null) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "用户不存在或密码错误");
		}
		// 校验密码
		if (!user.getUserPassword().equals(User.getEncryptPassword(userPassword))) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "用户不存在或密码错误");
		}
		// 判断是否被禁用
		if (UserDisabledEnum.isDisabled(user.getIsDisabled())) {
			throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "用户已被禁用");
		}
		// 存储用户登录态
		StpUtil.login(user.getId());
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
		redisCache.set(UserConstant.USER_LOGIN_STATE + user.getId()
				, JSONUtil.toJsonStr(user), tokenInfo.getTokenTimeout(), TimeUnit.SECONDS);
		return user;
	}

	/**
	 * 用户退出（退出登录）
	 *
	 * @return 是否成功
	 */
	public boolean userLogout() {
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
		redisCache.delete(UserConstant.USER_LOGIN_STATE + tokenInfo.getLoginId());
		StpUtil.logout();
		return true;
	}

	/**
	 * 编辑用户
	 *
	 * @param user 用户
	 * @return 是否成功
	 */
	public boolean editUser(User user) {
		boolean existed = userRepository.existedUserById(user.getId());
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
		}
		return userRepository.updateUser(user);
	}

	/**
	 * 上传头像
	 *
	 * @param avatarFile 头像文件
	 * @return 头像地址
	 */
	public String uploadAvatar(MultipartFile avatarFile) {
		long userId = StpUtil.getLoginIdAsLong();
		// 路径, 例如: images/public/2025_03_08/
		String pathPrefix = "avatar/" + userId + "/";
		// 调用上传图片
		UploadPictureResult uploadPictureResult = uploadPictureFile.uploadFile(avatarFile, pathPrefix, false);
		String avatarUrl = uploadPictureResult.getOriginUrl();
		if (StrUtil.isEmpty(avatarUrl)) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "头像上传失败");
		}
		boolean result = userRepository.updateUser(new User().setId(userId).setUserAvatar(avatarUrl));
		if (!result) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "头像更新失败");
		}
		return avatarUrl;
	}

	/**
	 * 删除用户
	 *
	 * @param userId 用户ID
	 * @return 是否成功
	 */
	public boolean deleteUser(Long userId) {
		boolean existed = userRepository.existedUserById(userId);
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
		}
		return userRepository.deleteUser(userId);
	}

	/**
	 * 更新用户
	 *
	 * @param user 用户
	 * @return 是否成功
	 */
	public boolean updateUser(User user) {
		boolean existed = userRepository.existedUserById(user.getId());
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
		}
		return userRepository.updateUser(user);
	}

	/**
	 * 重置用户密码
	 *
	 * @param userId 用户密码
	 * @return 重置后的密码
	 */
	public String resetPassword(Long userId) {
		boolean existed = userRepository.existedUserById(userId);
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
		}
		String tempPassword = RandomUtil.randomString(8);
		User user = new User().setId(userId).setUserPassword(User.getEncryptPassword(tempPassword));
		boolean result = userRepository.updateUser(user);
		if (!result) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "重置密码失败");
		}
		return tempPassword;
	}

	/**
	 * 禁用用户
	 *
	 * @param userId     用户 ID
	 * @param isDisabled 是否禁用
	 * @return 是否成功
	 */
	public boolean disabledUser(Long userId, Integer isDisabled) {
		boolean existed = userRepository.existedUserById(userId);
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
		}
		User user = new User().setId(userId).setIsDisabled(isDisabled);
		return userRepository.updateUser(user);
	}

	/**
	 * 获取用户信息
	 *
	 * @return 用户信息
	 */
	public User getUser() {
		return this.getUserByUserId(StpUtil.getLoginIdAsLong());
	}

	/**
	 * 根据用户ID获取用户信息
	 *
	 * @param userId 用户ID
	 * @return 用户信息
	 */
	public User getUserByUserId(Long userId) {
		String key = UserConstant.USER_LOGIN_STATE + userId;
		String userJson = redisCache.get(key);
		if (StrUtil.isEmpty(userJson)) {
			log.info("获取用户[{}]信息: MySQL...", userId);
			User user = userRepository.getUserByUserId(userId);
			if (user == null) {
				throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在");
			}
			// 7 天
			redisCache.set(key, JSONUtil.toJsonStr(user), 86400 * 7, TimeUnit.SECONDS);
			return user;
		} else {
			log.info("获取用户[{}]信息: Redis...", userId);
			return JSONUtil.toBean(userJson, User.class);
		}
	}

	/**
	 * 获取用户管理分页列表
	 *
	 * @param user 用户
	 * @return 用户管理分页列表
	 */
	public PageVO<User> getUserPageListAsManage(User user) {
		return userRepository.getUserPageList(user);
	}

	/**
	 * 根据用户ID集合获取用户列表
	 *
	 * @param userIds 用户ID集合
	 * @return 用户列表
	 */
	public List<User> getUserListByUserIds(Set<Long> userIds) {
		return userRepository.getUserListByUserIds(userIds);
	}

	// /**
	//  * 根据用户 ID 判断用户是否存在
	//  *
	//  * @param userId 用户 ID
	//  * @return 是否存在
	//  */
	// public Boolean existUserById(Long userId) {
	// 	return this.getBaseMapper().exists(new LambdaQueryWrapper<User>().eq(User::getId, userId));
	// }
	//
	// /**
	//  * 根据用户邮箱判断用户是否存在
	//  *
	//  * @param userEmail 用户邮箱
	//  * @return 是否存在
	//  */
	// public Boolean existUserByEmail(String userEmail) {
	// 	return this.getBaseMapper().exists(new LambdaQueryWrapper<User>().eq(User::getUserEmail, userEmail));
	// }
	//
	// /**
	//  * 根据用户 ID 集合获取用户列表
	//  *
	//  * @param userIds 用户 ID 集合
	//  * @return 用户列表
	//  */
	// public List<User> getUserListByIds(Set<Long> userIds) {
	// 	return this.listByIds(userIds);
	// }

	// // region ------- 以下代码为用户兑换会员功能 --------
	//
	// @Autowired
	// private ResourceLoader resourceLoader;
	//
	// // 文件读写锁（确保并发安全）
	// private final ReentrantLock fileLock = new ReentrantLock();
	//
	// /**
	//  * 用户兑换会员（会员码兑换）
	//  *
	//  * @param user    登录的用户
	//  * @param vipCode 会员码
	//  * @return 是否兑换成功
	//  */
	// @Override
	// public boolean exchangeVip(User user, String vipCode) {
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




