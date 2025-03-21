package com.baolong.pictures.domain.user.service;

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
import com.baolong.pictures.infrastructure.common.constant.CacheKeyConstant;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
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
 *
 * @author Baolong 2025年03月09 21:10
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserDomainService {
	// 领域服务
	private final UserRepository userRepository;
	// 基础设施
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
	 */
	public void userRegister(String userEmail, String codeKey, String codeValue) {
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
		if (result) return;
		throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败!");
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
		StpUtil.login(user.getUserId());
		SaTokenInfo tokenInfo = StpUtil.getTokenInfo();
		redisCache.set(UserConstant.USER_LOGIN_STATE + user.getUserId()
				, JSONUtil.toJsonStr(user), tokenInfo.getTokenTimeout(), TimeUnit.SECONDS);
		return user;
	}

	/**
	 * 用户退出（退出登录）
	 */
	public void userLogout() {
		redisCache.delete(UserConstant.USER_LOGIN_STATE + StpUtil.getLoginIdAsLong());
		StpUtil.logout();
	}

	/**
	 * 编辑用户
	 *
	 * @param user 用户领域对象
	 */
	public void editUser(User user) {
		boolean existed = userRepository.existedUserById(user.getUserId());
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
		}
		boolean result = userRepository.updateUser(user);
		if (result) return;
		throw new BusinessException(ErrorCode.SYSTEM_ERROR, "编辑失败!");
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
		boolean result = userRepository.updateUser(new User().setUserId(userId).setUserAvatar(avatarUrl));
		if (!result) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "头像更新失败");
		}
		return avatarUrl;
	}

	/**
	 * 删除用户
	 *
	 * @param userId 用户ID
	 */
	public void deleteUser(Long userId) {
		boolean existed = userRepository.existedUserById(userId);
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
		}
		boolean result = userRepository.deleteUser(userId);
		if (result) return;
		throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除失败!");
	}

	/**
	 * 更新用户
	 *
	 * @param user 用户领域对象
	 */
	public void updateUser(User user) {
		boolean existed = userRepository.existedUserById(user.getUserId());
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
		}
		boolean result = userRepository.updateUser(user);
		if (result) return;
		throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败!");
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
		User user = new User().setUserId(userId).setUserPassword(User.getEncryptPassword(tempPassword));
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
	 */
	public void disabledUser(Long userId, Integer isDisabled) {
		boolean existed = userRepository.existedUserById(userId);
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户不存在!");
		}
		User user = new User().setUserId(userId).setIsDisabled(isDisabled);
		boolean result = userRepository.updateUser(user);
		if (result) return;
		throw new BusinessException(ErrorCode.SYSTEM_ERROR, "禁用失败!");
	}

	/**
	 * 获取登录用户
	 *
	 * @return 登录用户
	 */
	public User getLoginUser() {
		return this.getUserByUserId(StpUtil.getLoginIdAsLong());
	}

	/**
	 * 根据用户ID获取用户
	 *
	 * @param userId 用户ID
	 * @return 用户
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
	 * @param user 用户领域对象
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
}
