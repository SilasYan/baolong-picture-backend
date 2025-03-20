package com.baolong.pictures.interfaces.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间更新请求
 */
@Data
public class SpaceUpdateRequest implements Serializable {

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
	 * 空间图片最大数量（单位: 张）
	 */
	private Long maxCount;

	private static final long serialVersionUID = 1L;
}
