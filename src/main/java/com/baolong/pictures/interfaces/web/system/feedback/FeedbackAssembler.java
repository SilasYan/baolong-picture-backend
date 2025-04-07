package com.baolong.pictures.interfaces.web.system.feedback;

import com.baolong.pictures.domain.system.feedback.aggregate.Feedback;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.system.feedback.request.FeedbackAddRequest;
import com.baolong.pictures.interfaces.web.system.feedback.request.FeedbackQueryRequest;
import com.baolong.pictures.interfaces.web.system.feedback.response.FeedbackVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户反馈转换类
 *
 * @author Baolong 2025年03月04 23:37
 * @version 1.0
 * @since 1.8
 */
public class FeedbackAssembler {

	/**
	 * 用户反馈更新请求 转为 用户反馈领域对象
	 */
	public static Feedback toDomain(FeedbackAddRequest feedbackAddRequest) {
		Feedback feedback = new Feedback();
		BeanUtils.copyProperties(feedbackAddRequest, feedback);
		return feedback;
	}

	/**
	 * 用户反馈查询请求 转为 用户反馈领域对象
	 */
	public static Feedback toDomain(FeedbackQueryRequest feedbackQueryRequest) {
		Feedback feedback = new Feedback();
		BeanUtils.copyProperties(feedbackQueryRequest, feedback);
		return feedback;
	}

	/**
	 * 用户反馈领域对象 转为 用户反馈响应对象
	 */
	public static FeedbackVO toVO(Feedback feedback) {
		FeedbackVO feedbackVO = new FeedbackVO();
		BeanUtils.copyProperties(feedback, feedbackVO);
		return feedbackVO;
	}

	/**
	 * 用户反馈领域对象分页 转为 用户反馈响应对象分页
	 */
	public static PageVO<FeedbackVO> toPageVO(PageVO<Feedback> feedbackPageVO) {
		return new PageVO<>(feedbackPageVO.getCurrent()
				, feedbackPageVO.getPageSize()
				, feedbackPageVO.getTotal()
				, feedbackPageVO.getPages()
				, Optional.ofNullable(feedbackPageVO.getRecords())
				.orElse(List.of()).stream()
				.map(FeedbackAssembler::toVO)
				.collect(Collectors.toList())
		);
	}
}
