package com.baolong.pictures.infrastructure.persistence.system.feedback.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.system.feedback.aggregate.Feedback;
import com.baolong.pictures.domain.system.feedback.repository.FeedbackRepository;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.system.feedback.converter.FeedbackConverter;
import com.baolong.pictures.infrastructure.persistence.system.feedback.mybatis.FeedbackDO;
import com.baolong.pictures.infrastructure.persistence.system.feedback.mybatis.FeedbackPersistenceService;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户反馈表 (feedback) - 仓储服务实现
 *
 * @author Silas Yan 2025-03-28:21:31
 */
@Repository
@RequiredArgsConstructor
public class FeedbackRepositoryImpl implements FeedbackRepository {

	private final FeedbackPersistenceService feedbackPersistenceService;

	/**
	 * 查询条件对象（Lambda）
	 *
	 * @param feedback 领域对象
	 * @return 查询条件对象（Lambda）
	 */
	private LambdaQueryWrapper<FeedbackDO> lambdaQueryWrapper(Feedback feedback) {
		LambdaQueryWrapper<FeedbackDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		Long feedbackId = feedback.getFeedbackId();
		Integer feedbackType = feedback.getFeedbackType();
		String content = feedback.getContent();
		Integer contactType = feedback.getContactType();
		String contactInfo = feedback.getContactInfo();
		Integer processStatus = feedback.getProcessStatus();
		String processContent = feedback.getProcessContent();
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(feedbackId), FeedbackDO::getId, feedbackId);
		lambdaQueryWrapper.eq(ObjUtil.isNotEmpty(feedbackType), FeedbackDO::getFeedbackType, feedbackType);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(content), FeedbackDO::getContent, content);
		lambdaQueryWrapper.eq(ObjUtil.isNotEmpty(contactType), FeedbackDO::getContactType, contactType);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(contactInfo), FeedbackDO::getContactInfo, contactInfo);
		lambdaQueryWrapper.eq(ObjUtil.isNotEmpty(processStatus), FeedbackDO::getProcessStatus, processStatus);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(processContent), FeedbackDO::getProcessContent, processContent);
		// 处理排序规则
		if (feedback.isMultipleSort()) {
			List<PageRequest.Sort> sorts = feedback.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(FeedbackDO.class, sortField));
				});
			}
		} else {
			PageRequest.Sort sort = feedback.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(FeedbackDO.class, sortField));
			} else {
				lambdaQueryWrapper.orderByDesc(FeedbackDO::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	/**
	 * 新增用户反馈
	 *
	 * @param feedback 用户反馈领域对象
	 */
	@Override
	public boolean addFeedback(Feedback feedback) {
		FeedbackDO feedbackDO = FeedbackConverter.toDO(feedback);
		return feedbackPersistenceService.save(feedbackDO);
	}

	/**
	 * 获取用户反馈管理分页列表
	 *
	 * @param feedback 用户反馈领域对象
	 * @return 用户反馈管理分页列表
	 */
	@Override
	public PageVO<Feedback> getFeedbackPageList(Feedback feedback) {
		LambdaQueryWrapper<FeedbackDO> lambdaQueryWrapper = this.lambdaQueryWrapper(feedback);
		Page<FeedbackDO> page = feedbackPersistenceService.page(feedback.getPage(FeedbackDO.class), lambdaQueryWrapper);
		return FeedbackConverter.toDomainPage(page);
	}
}
