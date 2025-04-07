package com.baolong.pictures.infrastructure.persistence.system.task.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.baolong.pictures.domain.system.task.aggregate.ScheduledTask;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.system.task.mybatis.ScheduledTaskDO;
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
public class ScheduledTaskConverter {

	private final static CopyOptions toDoOption = CopyOptions.create();
	private final static CopyOptions toDomainOption = CopyOptions.create();

	static {
		toDoOption.setFieldMapping(MapUtil.of("taskId", "id"));
		toDomainOption.setFieldMapping(MapUtil.of("id", "taskId"));
	}

	/**
	 * 领域模型 转为 持久化模型
	 *
	 * @param user 领域模型
	 * @return 持久化模型
	 */
	public static ScheduledTaskDO toDO(ScheduledTask user) {
		ScheduledTaskDO userDO = new ScheduledTaskDO();
		BeanUtil.copyProperties(user, userDO, toDoOption);
		return userDO;
	}

	/**
	 * 领域模型列表 转为 持久化模型列表
	 */
	public static List<ScheduledTaskDO> toDOList(List<ScheduledTask> userList) {
		return Optional.ofNullable(userList)
				.orElse(List.of()).stream()
				.map(ScheduledTaskConverter::toDO)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型 转为 领域模型
	 *
	 * @param userDO 持久化模型
	 * @return 领域模型
	 */
	public static ScheduledTask toDomain(ScheduledTaskDO userDO) {
		ScheduledTask user = new ScheduledTask();
		BeanUtil.copyProperties(userDO, user, toDomainOption);
		return user;
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 *
	 * @param userDOList 持久化模型列表
	 * @return 领域模型列表
	 */
	public static List<ScheduledTask> toDomainList(List<ScheduledTaskDO> userDOList) {
		return Optional.ofNullable(userDOList)
				.orElse(List.of())
				.stream().map(ScheduledTaskConverter::toDomain)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 *
	 * @param userDOPage 持久化模型分页
	 * @return 领域模型分页
	 */
	public static PageVO<ScheduledTask> toDomainPage(Page<ScheduledTaskDO> userDOPage) {
		return new PageVO<>(
				userDOPage.getCurrent()
				, userDOPage.getSize()
				, userDOPage.getTotal()
				, userDOPage.getPages()
				, Optional.ofNullable(userDOPage.getRecords())
				.orElse(List.of())
				.stream().map(ScheduledTaskConverter::toDomain)
				.collect(Collectors.toList())
		);
	}
}
