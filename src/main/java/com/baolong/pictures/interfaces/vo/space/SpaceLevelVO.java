package com.baolong.pictures.interfaces.vo.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间等级 VO
 */
@Data
public class SpaceLevelVO implements Serializable {

	/**
	 * 空间等级 Key
	 */
	private Integer key;

	/**
	 * 空间等级名称
	 */
	private String label;

	/**
	 * 空间等级最大大小
	 */
	private Long maxSize;

	/**
	 * 空间等级最大数量
	 */
	private Long maxCount;

	private static final long serialVersionUID = 1L;
}
