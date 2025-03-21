package com.baolong.pictures.infrastructure.persistence.space.space.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 空间表
 *
 * @TableName space
 */
@TableName(value = "space")
@Data
public class SpaceDO implements Serializable {

	/**
	 * 主键 ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 空间名称
	 */
	private String spaceName;

	/**
	 * 空间类型（0-私有空间, 1-团队空间）
	 */
	private Integer spaceType;

	/**
	 * 空间级别（0-普通版, 1-专业版, 2-旗舰版）
	 */
	private Integer spaceLevel;

	/**
	 * 空间图片最大大小（单位: B）
	 */
	private Long maxSize;

	/**
	 * 空间图片最大数量（单位: 张）
	 */
	private Long maxCount;

	/**
	 * 空间使用大小（单位: B）
	 */
	private Long usedSize;

	/**
	 * 空间使用数量（单位: 张）
	 */
	private Long usedCount;

	/**
	 * 创建用户 ID
	 */
	private Long userId;

	/**
	 * 是否删除
	 */
	private Integer isDelete;

	/**
	 * 编辑时间
	 */
	@TableField(value = "edit_time", fill = FieldFill.UPDATE)
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
