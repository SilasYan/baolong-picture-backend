package com.baolong.pictures.domain.system.feedback.service;

import com.baolong.pictures.domain.system.feedback.aggregate.Feedback;
import com.baolong.pictures.domain.system.feedback.repository.FeedbackRepository;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.manager.message.EmailManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户反馈表 (feedback) - 领域服务
 *
 * @author Silas Yan 2025-03-28:21:31
 */
@Service
@RequiredArgsConstructor
public class FeedbackDomainService {
	private final FeedbackRepository feedbackRepository;
	private final EmailManager emailManager;

	/**
	 * 新增用户反馈
	 *
	 * @param feedback 用户反馈领域对象
	 */
	public void addFeedback(Feedback feedback) {
		boolean result = feedbackRepository.addFeedback(feedback);
		if (!result) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户反馈失败");
		}
		emailManager.sendEmailAsText("收到新的用户反馈", feedback.getContent());

	}

	/**
	 * 获取用户反馈管理分页列表
	 *
	 * @param feedback 用户反馈领域对象
	 * @return 用户反馈管理分页列表
	 */
	public PageVO<Feedback> getFeedbackPageList(Feedback feedback) {
		return feedbackRepository.getFeedbackPageList(feedback);
	}
}
