package com.baolong.pictures.infrastructure.persistence.system.feedback.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.baolong.pictures.domain.system.feedback.aggregate.Feedback;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.system.feedback.mybatis.FeedbackDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模型转化类（领域模型 <=> 持久化模型）
 *
 * @author Baolong 2025年03月20 20:42
 * @version 1.0
 * @since 1.8
 */
public class FeedbackConverter {

	private final static CopyOptions toDoOption = CopyOptions.create();
	private final static CopyOptions toDomainOption = CopyOptions.create();

	static {
		toDoOption.setFieldMapping(MapUtil.of("feedbackId", "id"));
		toDomainOption.setFieldMapping(MapUtil.of("id", "feedbackId"));
	}

	/**
	 * 领域模型 转为 持久化模型
	 *
	 * @param feedback 领域模型
	 * @return 持久化模型
	 */
	public static FeedbackDO toDO(Feedback feedback) {
		FeedbackDO feedbackDO = new FeedbackDO();
		BeanUtil.copyProperties(feedback, feedbackDO, toDoOption);
		return feedbackDO;
	}

	/**
	 * 领域模型列表 转为 持久化模型列表
	 */
	public static List<FeedbackDO> toDOList(List<Feedback> feedbackList) {
		return Optional.ofNullable(feedbackList)
				.orElse(List.of()).stream()
				.map(FeedbackConverter::toDO)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型 转为 领域模型
	 *
	 * @param feedbackDO 持久化模型
	 * @return 领域模型
	 */
	public static Feedback toDomain(FeedbackDO feedbackDO) {
		Feedback feedback = new Feedback();
		BeanUtil.copyProperties(feedbackDO, feedback, toDomainOption);
		return feedback;
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 *
	 * @param feedbackDOList 持久化模型列表
	 * @return 领域模型列表
	 */
	public static List<Feedback> toDomainList(List<FeedbackDO> feedbackDOList) {
		return Optional.ofNullable(feedbackDOList)
				.orElse(List.of())
				.stream().map(FeedbackConverter::toDomain)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 *
	 * @param feedbackDOPage 持久化模型分页
	 * @return 领域模型分页
	 */
	public static PageVO<Feedback> toDomainPage(Page<FeedbackDO> feedbackDOPage) {
		return new PageVO<>(
				feedbackDOPage.getCurrent()
				, feedbackDOPage.getSize()
				, feedbackDOPage.getTotal()
				, feedbackDOPage.getPages()
				, Optional.ofNullable(feedbackDOPage.getRecords())
				.orElse(List.of())
				.stream().map(FeedbackConverter::toDomain)
				.collect(Collectors.toList())
		);
	}
}
