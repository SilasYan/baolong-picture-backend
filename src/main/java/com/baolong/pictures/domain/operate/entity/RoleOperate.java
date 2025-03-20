package com.baolong.pictures.domain.operate.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 角色操作关联表
 * @TableName role_operate
 */
@TableName(value ="role_operate")
@Data
public class RoleOperate implements Serializable {
    /**
     * 
     */
    private String roleKey;

    /**
     * 
     */
    private Long operateId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
