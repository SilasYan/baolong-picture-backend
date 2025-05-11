package com.baolong.pictures.infrastructure.persistence.space.spaceUser.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.baolong.pictures.domain.space.aggregate.SpaceUser;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.space.spaceUser.mybatis.SpaceUserDO;
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
public class SpaceUserConverter {

	private final static CopyOptions toDoOption = CopyOptions.create();
	private final static CopyOptions toDomainOption = CopyOptions.create();

	static {
		toDoOption.setFieldMapping(MapUtil.of("spaceUserId", "id"));
		toDomainOption.setFieldMapping(MapUtil.of("id", "spaceUserId"));
	}

	/**
	 * 领域模型 转为 持久化模型
	 *
	 * @param spaceUser 领域模型
	 * @return 持久化模型
	 */
	public static SpaceUserDO toDO(SpaceUser spaceUser) {
		SpaceUserDO spaceUserDO = new SpaceUserDO();
		BeanUtil.copyProperties(spaceUser, spaceUserDO, toDoOption);
		return spaceUserDO;
	}

	/**
	 * 持久化模型 转为 领域模型
	 *
	 * @param spaceUserDO 持久化模型
	 * @return 领域模型
	 */
	public static SpaceUser toDomain(SpaceUserDO spaceUserDO) {
		SpaceUser spaceUser = new SpaceUser();
		BeanUtil.copyProperties(spaceUserDO, spaceUser, toDomainOption);
		return spaceUser;
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 *
	 * @param userDOPage 持久化模型分页
	 * @return 领域模型分页
	 */
	public static PageVO<SpaceUser> toDomainPage(Page<SpaceUserDO> userDOPage) {
		List<SpaceUser> userList = Optional.ofNullable(userDOPage.getRecords())
				.orElse(List.of())
				.stream().map(SpaceUserConverter::toDomain)
				.collect(Collectors.toList());
		return new PageVO<>(
				userDOPage.getCurrent()
				, userDOPage.getSize()
				, userDOPage.getTotal()
				, userDOPage.getPages()
				, userList
		);
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 *
	 * @param userDOList 持久化模型列表
	 * @return 领域模型列表
	 */
	public static List<SpaceUser> toDomainList(List<SpaceUserDO> userDOList) {
		return Optional.ofNullable(userDOList)
				.orElse(List.of())
				.stream().map(SpaceUserConverter::toDomain)
				.collect(Collectors.toList());
	}
}
