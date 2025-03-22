package com.baolong.pictures.infrastructure.persistence.system.task.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.system.task.aggregate.ScheduledTask;
import com.baolong.pictures.domain.system.task.repository.ScheduledTaskRepository;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.system.task.converter.ScheduledTaskConverter;
import com.baolong.pictures.infrastructure.persistence.system.task.mybatis.ScheduledTaskDO;
import com.baolong.pictures.infrastructure.persistence.system.task.mybatis.ScheduledTaskPersistenceService;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 定时任务表 (scheduled_task) - 仓储服务实现
 *
 * @author Silas Yan 2025-03-22:15:52
 */
@Repository
@RequiredArgsConstructor
public class ScheduledTaskRepositoryImpl implements ScheduledTaskRepository {

	private final ScheduledTaskPersistenceService scheduledTaskPersistenceService;

	/**
	 * 查询条件对象（Lambda）
	 *
	 * @param scheduledTask 定时任务领域对象
	 * @return 查询条件对象（Lambda）
	 */
	private LambdaQueryWrapper<ScheduledTaskDO> lambdaQueryWrapper(ScheduledTask scheduledTask) {
		LambdaQueryWrapper<ScheduledTaskDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		Long taskId = scheduledTask.getTaskId();
		String jobKey = scheduledTask.getTaskKey();
		String jobName = scheduledTask.getTaskName();
		String jobCron = scheduledTask.getTaskCron();
		String jobDesc = scheduledTask.getTaskDesc();
		String jobBean = scheduledTask.getTaskBean();
		Integer jobStatus = scheduledTask.getTaskStatus();
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(taskId), ScheduledTaskDO::getId, taskId);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(jobKey), ScheduledTaskDO::getTaskKey, jobKey);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(jobName), ScheduledTaskDO::getTaskName, jobName);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(jobCron), ScheduledTaskDO::getTaskCron, jobCron);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(jobDesc), ScheduledTaskDO::getTaskDesc, jobDesc);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(jobBean), ScheduledTaskDO::getTaskBean, jobBean);
		lambdaQueryWrapper.eq(ObjUtil.isNotEmpty(jobStatus), ScheduledTaskDO::getTaskStatus, jobStatus);
		// 处理排序规则
		if (scheduledTask.isMultipleSort()) {
			List<PageRequest.Sort> sorts = scheduledTask.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(ScheduledTaskDO.class, sortField));
				});
			}
		} else {
			PageRequest.Sort sort = scheduledTask.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(ScheduledTaskDO.class, sortField));
			} else {
				lambdaQueryWrapper.orderByDesc(ScheduledTaskDO::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	/**
	 * 新增定时任务
	 *
	 * @param scheduledTask 定时任务领域对象
	 * @return 是否成功
	 */
	@Override
	public boolean addScheduledTask(ScheduledTask scheduledTask) {
		return scheduledTaskPersistenceService.save(ScheduledTaskConverter.toDO(scheduledTask));
	}

	/**
	 * 删除定时任务
	 *
	 * @param taskId 任务ID
	 * @return 是否成功
	 */
	@Override
	public boolean deleteScheduledTask(Long taskId) {
		return scheduledTaskPersistenceService.removeById(taskId);
	}

	/**
	 * 更新定时任务
	 *
	 * @param scheduledTask 定时任务领域对象
	 */
	@Override
	public boolean updateScheduledTask(ScheduledTask scheduledTask) {
		return scheduledTaskPersistenceService.updateById(ScheduledTaskConverter.toDO(scheduledTask));
	}

	/**
	 * 根据任务ID判断任务是否存在
	 *
	 * @param taskId 任务ID
	 * @return 是否存在
	 */
	@Override
	public boolean existedScheduledTaskByTaskId(Long taskId) {
		return scheduledTaskPersistenceService.getBaseMapper()
				.exists(new LambdaQueryWrapper<ScheduledTaskDO>()
						.eq(ScheduledTaskDO::getId, taskId)
				);
	}

	/**
	 * 根据任务KEY或任务方法判断任务是否存在
	 *
	 * @param taskBean 任务方法/任务KEY
	 * @return 是否存在
	 */
	@Override
	public boolean existedScheduledTaskByTaskKeyOrTaskBean(String taskBean) {
		return scheduledTaskPersistenceService.getBaseMapper()
				.exists(new LambdaQueryWrapper<ScheduledTaskDO>()
						.eq(ScheduledTaskDO::getTaskBean, taskBean)
						.or()
						.eq(ScheduledTaskDO::getTaskKey, taskBean)
				);
	}

	/**
	 * 获取定时任务管理分页列表
	 *
	 * @param scheduledTask 定时任务领域对象
	 * @return 定时任务管理分页列表
	 */
	@Override
	public PageVO<ScheduledTask> getScheduledTaskPage(ScheduledTask scheduledTask) {
		LambdaQueryWrapper<ScheduledTaskDO> lambdaQueryWrapper = this.lambdaQueryWrapper(scheduledTask);
		Page<ScheduledTaskDO> page = scheduledTaskPersistenceService.page(scheduledTask.getPage(ScheduledTaskDO.class), lambdaQueryWrapper);
		return ScheduledTaskConverter.toDomainPage(page);
	}

	/**
	 * 根据任务方法获取定时任务
	 *
	 * @param taskBean 任务方法
	 * @return 定时任务
	 */
	@Override
	public ScheduledTask getScheduledTaskByTaskBean(String taskBean) {
		ScheduledTaskDO scheduledTaskDO = scheduledTaskPersistenceService.getOne(
				new LambdaQueryWrapper<ScheduledTaskDO>().eq(ScheduledTaskDO::getTaskBean, taskBean)
		);
		return ScheduledTaskConverter.toDomain(scheduledTaskDO);
	}
}
