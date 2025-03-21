package com.baolong.pictures.infrastructure.persistence.space.space.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.space.space.mybatis.SpaceDO;
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
public class SpaceConverter {

	private final static CopyOptions toDoOption = CopyOptions.create();
	private final static CopyOptions toDomainOption = CopyOptions.create();

	static {
		toDoOption.setFieldMapping(MapUtil.of("spaceId", "id"));
		toDomainOption.setFieldMapping(MapUtil.of("id", "spaceId"));
	}

	/**
	 * 领域模型 转为 持久化模型
	 *
	 * @param space 领域模型
	 * @return 持久化模型
	 */
	public static SpaceDO toDO(Space space) {
		SpaceDO spaceDO = new SpaceDO();
		BeanUtil.copyProperties(space, spaceDO, toDoOption);
		return spaceDO;
	}

	/**
	 * 领域模型列表 转为 持久化模型列表
	 */
	public static List<SpaceDO> toDOList(List<Space> spaceList) {
		return Optional.ofNullable(spaceList)
				.orElse(List.of()).stream()
				.map(SpaceConverter::toDO)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型 转为 领域模型
	 *
	 * @param spaceDO 持久化模型
	 * @return 领域模型
	 */
	public static Space toDomain(SpaceDO spaceDO) {
		Space space = new Space();
		BeanUtil.copyProperties(spaceDO, space, toDomainOption);
		return space;
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 *
	 * @param userDOList 持久化模型列表
	 * @return 领域模型列表
	 */
	public static List<Space> toDomainList(List<SpaceDO> userDOList) {
		return Optional.ofNullable(userDOList)
				.orElse(List.of())
				.stream().map(SpaceConverter::toDomain)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 *
	 * @param userDOPage 持久化模型分页
	 * @return 领域模型分页
	 */
	public static PageVO<Space> toDomainPage(Page<SpaceDO> userDOPage) {
		return new PageVO<>(
				userDOPage.getCurrent()
				, userDOPage.getSize()
				, userDOPage.getTotal()
				, userDOPage.getPages()
				, Optional.ofNullable(userDOPage.getRecords())
				.orElse(List.of())
				.stream().map(SpaceConverter::toDomain)
				.collect(Collectors.toList())
		);
	}
}
