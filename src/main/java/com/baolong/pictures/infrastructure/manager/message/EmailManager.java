package com.baolong.pictures.infrastructure.manager.message;

import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.utils.EmailUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邮箱服务
 *
 * @author Baolong 2025年03月06 22:39
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Component
public class EmailManager {
	@Resource
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.nickname}")
	private String nickname;

	@Value("${spring.mail.username}")
	private String from;

	@Async(value = "emailThreadPool")
	public void sendEmailCode(String to, String subject, String code) {
		log.info("发送邮件验证码[{}]到[{}]", to, code);
		Map<String, Object> map = new HashMap<>();
		map.put("code", code);
		sendEmailAsCode(to, subject, map);
	}

	/**
	 * 发送验证码邮件
	 */
	public void sendEmailAsCode(String to, String subject, Map<String, Object> contentMap) {
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			// 组合邮箱发送的内容
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
			// 设置邮件发送者
			messageHelper.setFrom(nickname + "<" + from + ">");
			// 设置邮件接收者
			messageHelper.setTo(to);
			// 设置邮件标题
			messageHelper.setSubject(subject);
			// 设置邮件内容
			messageHelper.setText(EmailUtils.emailContentTemplate("templates/EmailCodeTemplate.html", contentMap), true);
			javaMailSender.send(message);
		} catch (MessagingException e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送邮件失败");
		}
	}

	/**
	 * 发送注册成功邮件
	 */
	@Async(value = "emailThreadPool")
	public void sendEmailAsRegisterSuccess(String to, String subject) {
		try {
			// 创建MIME消息
			MimeMessage message = javaMailSender.createMimeMessage();
			// 组合邮箱发送的内容
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
			// 设置邮件发送者
			messageHelper.setFrom(nickname + "<" + from + ">");
			// 设置邮件接收者
			messageHelper.setTo(to);
			// 设置邮件主题
			messageHelper.setSubject(subject);
			// 设置邮件内容
			messageHelper.setText(EmailUtils.emailContentTemplate("templates/EmailRegisterSuccessTemplate.html"), true);
			// 发送邮件
			javaMailSender.send(message);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送邮件失败");
		}
	}

	/**
	 * 发送文本邮件
	 */
	@Async(value = "emailThreadPool")
	public void sendEmailAsText(String title, String text) {
		try {
			// 创建邮件对象
			SimpleMailMessage smm = new SimpleMailMessage();
			// 设置邮件发送者
			smm.setFrom(nickname + "<" + from + ">");
			// 设置邮件接收者
			smm.setTo("510132075@qq.com");
			// 设置邮件主题
			smm.setSubject(title);
			// 设置邮件内容
			smm.setText(text);
			// 发送邮件
			javaMailSender.send(smm);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送邮件失败");
		}
	}

	/**
	 * 发送审核邮件
	 */
	@Async(value = "emailThreadPool")
	public void sendEmailAsReview(List<String> tos, String subject, String reviewStatus) {
		tos.forEach(to->{
			sendEmailAsReview(to, subject, reviewStatus);
		});
	}

	/**
	 * 发送审核邮件
	 */
	public void sendEmailAsReview(String to, String subject, String reviewStatus) {
		try {
			// 创建MIME消息
			MimeMessage message = javaMailSender.createMimeMessage();
			// 组合邮箱发送的内容
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
			// 设置邮件发送者
			messageHelper.setFrom(nickname + "<" + from + ">");
			// 设置邮件接收者
			messageHelper.setTo(to);
			// 设置邮件主题
			messageHelper.setSubject(subject);
			// 设置邮件内容
			Map<String, Object> contentMap = new HashMap<>();
			contentMap.put("reviewStatus", reviewStatus);
			messageHelper.setText(EmailUtils.emailContentTemplate("templates/EmailReviewTemplate.html", contentMap), true);
			// 发送邮件
			javaMailSender.send(message);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "发送邮件失败");
		}
	}
}
