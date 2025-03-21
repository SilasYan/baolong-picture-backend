package com.baolong.pictures.interfaces.web.space.response;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 空间详情响应对象
 */
@Data
public class SpaceDetailVO implements Serializable {

	/**
	 * 空间ID
	 */
	private Long spaceId;

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
	 * 创建时间
	 */
	private Date createTime;

	private static final long serialVersionUID = 1L;
}
