package com.baolong.pictures.interfaces.vo.space;

import com.baolong.pictures.domain.user.entity.User;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 空间 VO
 */
@Data
public class SpaceVO implements Serializable {

	/**
	 * 空间 ID
	 */
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
	 * 空间图片最大大小（加上单位的）
	 */
	private String maxSizeUnit;

	/**
	 * 空间图片最大数量（单位: 张）
	 */
	private Long maxCount;

	/**
	 * 空间使用大小（单位: B）
	 */
	private Long usedSize;

	/**
	 * 空间使用大小（加上单位的）
	 */
	private String usedSizeUnit;

	/**
	 * 空间使用数量（单位: 张）
	 */
	private Long usedCount;

	/**
	 * 创建用户 ID
	 */
	private Long userId;

	/**
	 * 用户信息
	 */
	private User userInfo;

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

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}
