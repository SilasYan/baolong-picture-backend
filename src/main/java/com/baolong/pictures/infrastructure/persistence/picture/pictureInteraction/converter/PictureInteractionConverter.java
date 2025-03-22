package com.baolong.pictures.infrastructure.persistence.picture.pictureInteraction.converter;

import cn.hutool.core.bean.BeanUtil;
import com.baolong.pictures.domain.picture.aggregate.PictureInteraction;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.picture.pictureInteraction.mybatis.PictureInteractionDO;
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
public class PictureInteractionConverter {

	/**
	 * 领域模型 转为 持久化模型
	 */
	public static PictureInteractionDO toDO(PictureInteraction pictureInteraction) {
		PictureInteractionDO pictureInteractionDO = new PictureInteractionDO();
		BeanUtil.copyProperties(pictureInteraction, pictureInteractionDO);
		return pictureInteractionDO;
	}

	/**
	 * 持久化模型 转为 领域模型
	 */
	public static PictureInteraction toDomain(PictureInteractionDO pictureInteractionDO) {
		PictureInteraction pictureInteraction = new PictureInteraction();
		BeanUtil.copyProperties(pictureInteractionDO, pictureInteraction);
		return pictureInteraction;
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 *
	 * @param userDOList 持久化模型列表
	 * @return 领域模型列表
	 */
	public static List<PictureInteraction> toDomainList(List<PictureInteractionDO> userDOList) {
		return Optional.ofNullable(userDOList)
				.orElse(List.of())
				.stream().map(PictureInteractionConverter::toDomain)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 */
	public static PageVO<PictureInteraction> toDomainPage(Page<PictureInteractionDO> userInteractionDOPage) {
		return new PageVO<>(
				userInteractionDOPage.getCurrent()
				, userInteractionDOPage.getSize()
				, userInteractionDOPage.getTotal()
				, userInteractionDOPage.getPages()
				, Optional.ofNullable(userInteractionDOPage.getRecords())
				.orElse(List.of())
				.stream().map(PictureInteractionConverter::toDomain)
				.collect(Collectors.toList())
		);
	}
}
