package com.baolong.pictures.infrastructure.manager.message;

import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.utils.EmailUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Map;

/**
 * 邮箱服务
 *
 * @author Baolong 2025年03月06 22:39
 * @version 1.0
 * @since 1.8
 */
@Component
public class EmailManager {
	@Resource
	private JavaMailSender javaMailSender;

	@Value("${spring.mail.nickname}")
	private String nickname;

	@Value("${spring.mail.username}")
	private String from;

	public void sendEmail(String to, String subject, Map<String, Object> contentMap) {
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
}
