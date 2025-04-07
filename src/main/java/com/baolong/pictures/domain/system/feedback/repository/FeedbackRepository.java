package com.baolong.pictures.domain.system.feedback.repository;

import com.baolong.pictures.domain.system.feedback.aggregate.Feedback;
import com.baolong.pictures.infrastructure.common.page.PageVO;

/**
 * 用户反馈表 (feedback) - 仓储服务接口
 *
 * @author Silas Yan 2025-03-28:21:30
 */
public interface FeedbackRepository {

	/**
	 * 新增用户反馈
	 *
	 * @param feedback 用户反馈领域对象
	 */
	boolean addFeedback(Feedback feedback);

	/**
	 * 获取用户反馈管理分页列表
	 *
	 * @param feedback 用户反馈领域对象
	 * @return 用户反馈管理分页列表
	 */
	PageVO<Feedback> getFeedbackPageList(Feedback feedback);
}
