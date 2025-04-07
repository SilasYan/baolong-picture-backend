package com.baolong.pictures.domain.system.task.aggregate;

import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.scheduling.support.CronTrigger;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时任务领域对象
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class ScheduledTask extends PageRequest implements Serializable {

    // region 原始属性

    /**
     * 任务ID
     */
    private Long taskId;

    /**
     * 任务 KEY（存在内存中）
     */
    private String taskKey;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务 corn 表达式
     */
    private String taskCron;

    /**
     * 任务描述
     */
    private String taskDesc;

    /**
     * 任务 Bean 名称（执行任务的 bean）
     */
    private String taskBean;

    /**
     * 任务状态（0-关闭, 1-开启）
     */
    private Integer taskStatus;

    /**
     * 是否删除（0-正常, 1-删除）
     */
    private Integer isDelete;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    // endregion 原始属性

    // region 领域方法

    /**
     * 校验参数
     */
    public void checkParam() {
        ThrowUtils.throwIf(StrUtil.isEmpty(this.taskName), ErrorCode.PARAMS_ERROR, "定时任务名称不能为空");
        ThrowUtils.throwIf(StrUtil.isEmpty(this.taskCron), ErrorCode.PARAMS_ERROR, "任务表达式不能为空");
        ThrowUtils.throwIf(StrUtil.isEmpty(this.taskBean), ErrorCode.PARAMS_ERROR, "目标方法不能为空");
		try {
            new CronTrigger(this.taskCron);
		} catch (Exception e) {
            throw new BusinessException(ErrorCode.DATA_ERROR, "任务表达式错误");
		}
	}

    // endregion 领域方法

    private static final long serialVersionUID = 1L;
}
