package com.baolong.pictures.application.service.impl;

import com.baolong.pictures.application.service.MainApplicationService;
import com.baolong.pictures.infrastructure.constant.CacheKeyConstant;
import com.baolong.pictures.infrastructure.manager.redis.RedisCache;
import com.baolong.pictures.interfaces.vo.CaptchaVO;
import com.wf.captcha.SpecCaptcha;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 公共应用服务实现
 *
 * @author Baolong 2025年03月06 23:14
 * @version 1.0
 * @since 1.8
 */
@Service
public class MainApplicationServiceImpl implements MainApplicationService {
	@Resource
	private RedisCache redisCache;

	/**
	 * 获取图形验证码
	 *
	 * @return 图形验证码VO
	 */
	@Override
	public CaptchaVO getCaptcha() {
		SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 5);
		String captchaCode = specCaptcha.text().toLowerCase();
		String captchaImage = specCaptcha.toBase64();
		String captchaKey = UUID.randomUUID().toString();
		// 把验证码存入 Redis 并且设置 1 分钟过期
		redisCache.set(String.format(CacheKeyConstant.CAPTCHA_CODE_KEY, captchaKey), captchaCode, 1, TimeUnit.MINUTES);
		CaptchaVO captchaVO = new CaptchaVO();
		captchaVO.setCaptchaKey(captchaKey);
		captchaVO.setCaptchaImage(captchaImage);
		return captchaVO;
	}
}
