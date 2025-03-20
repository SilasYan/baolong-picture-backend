package com.baolong.pictures.domain.operate.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作表
 * @TableName operate
 */
@TableName(value ="operate")
@Data
public class Operate implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 操作名称
     */
    private String operateName;

    /**
     * 操作标识
     */
    private String operateKey;

    /**
     * 操作描述
     */
    private String operateDesc;

    /**
     * 是否禁用（0-正常, 1-禁用）
     */
    private Integer isDisabled;

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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
