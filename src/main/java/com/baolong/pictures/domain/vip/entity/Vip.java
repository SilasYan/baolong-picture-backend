package com.baolong.pictures.domain.vip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 会员表
 *
 * @TableName vip
 */
@TableName(value = "vip")
@Data
public class Vip implements Serializable {
	/**
	 * 主键 ID
	 */
	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 会员码
	 */
	private String code;

	/**
	 * 会员类型（VIP-普通会员, SVIP-超级会员）
	 */
	private String type;

	/**
	 * 时长
	 */
	private Integer duration;

	/**
	 * 时长单位（DAY-天, MONTH-月, YEAR-年, PERM-永久）
	 */
	private String unit;

	/**
	 * 状态（0-未使用, 1-已使用, 2-已过期）
	 */
	private Integer status;

	/**
	 * 使用用户 ID
	 */
	private Long usedUser;

	/**
	 * 使用时间
	 */
	private Date usedTime;

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
