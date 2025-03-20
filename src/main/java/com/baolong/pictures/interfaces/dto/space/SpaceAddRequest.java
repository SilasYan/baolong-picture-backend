package com.baolong.pictures.interfaces.dto.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间创建请求
 */
@Data
public class SpaceAddRequest implements Serializable {

	/**
	 * 用户 ID
	 */
	private Long userId;

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

	private static final long serialVersionUID = 1L;
}
