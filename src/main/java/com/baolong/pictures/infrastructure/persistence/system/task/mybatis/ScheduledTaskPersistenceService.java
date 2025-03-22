package com.baolong.pictures.infrastructure.persistence.system.task.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 定时任务表 (scheduled_task) - 持久化服务
 *
 * @author Baolong 2025-03-22 15:38:18
 */
@Service
public class ScheduledTaskPersistenceService extends ServiceImpl<ScheduledTaskMapper, ScheduledTaskDO> {
}
