package com.baolong.pictures.interfaces.rest.system;

import com.baolong.pictures.application.service.system.FeedbackApplicationService;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.system.feedback.aggregate.Feedback;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.system.feedback.FeedbackAssembler;
import com.baolong.pictures.interfaces.web.system.feedback.request.FeedbackAddRequest;
import com.baolong.pictures.interfaces.web.system.feedback.request.FeedbackQueryRequest;
import com.baolong.pictures.interfaces.web.system.feedback.response.FeedbackVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户反馈表 (feedback) - 接口
 *
 * @author Silas Yan 2025-03-28:21:33
 */
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

	private final FeedbackApplicationService feedbackApplicationService;

	/**
	 * 新增用户反馈
	 *
	 * @param feedbackAddRequest 用户反馈新增请求
	 * @return 新增结果
	 */
	@PostMapping("/add")
	public BaseResponse<Boolean> addFeedback(@RequestBody FeedbackAddRequest feedbackAddRequest) {
		ThrowUtils.throwIf(feedbackAddRequest == null, ErrorCode.PARAMS_ERROR);
		Feedback feedback = FeedbackAssembler.toDomain(feedbackAddRequest);
		feedback.checkAddRequest();
		feedbackApplicationService.addFeedback(feedback);
		return ResultUtils.success();
	}

	/**
	 * 获取用户反馈管理分页列表
	 *
	 * @param feedbackQueryRequest 用户反馈查询对象
	 * @return 用户反馈管理分页列表
	 */
	@PostMapping("/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<FeedbackVO>> getFeedbackPageList(
			@RequestBody FeedbackQueryRequest feedbackQueryRequest) {
		ThrowUtils.throwIf(feedbackQueryRequest == null, ErrorCode.PARAMS_ERROR);
		Feedback feedback = FeedbackAssembler.toDomain(feedbackQueryRequest);
		PageVO<Feedback> scheduledTaskPageVO = feedbackApplicationService.getFeedbackPageList(feedback);
		return ResultUtils.success(FeedbackAssembler.toPageVO(scheduledTaskPageVO));
	}
}
