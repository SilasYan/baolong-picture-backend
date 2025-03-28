package com.baolong.pictures.application.service.system;

import com.baolong.pictures.domain.system.feedback.aggregate.Feedback;
import com.baolong.pictures.domain.system.feedback.service.FeedbackDomainService;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 用户反馈表 (feedback) - 应用服务
 *
 * @author Silas Yan 2025-03-28:21:32
 */
@Service
@RequiredArgsConstructor
public class FeedbackApplicationService {

	private final FeedbackDomainService feedbackDomainService;

	/**
	 * 新增用户反馈
	 *
	 * @param feedback 用户反馈领域对象
	 */
	public void addFeedback(Feedback feedback) {
		feedbackDomainService.addFeedback(feedback);
	}

	/**
	 * 获取用户反馈管理分页列表
	 *
	 * @param feedback 用户反馈领域对象
	 * @return 用户反馈管理分页列表
	 */
	public PageVO<Feedback> getFeedbackPageList(Feedback feedback) {
		return feedbackDomainService.getFeedbackPageList(feedback);
	}
}
