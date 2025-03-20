package com.baolong.pictures.interfaces.vo.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间资源分析响应
 */
@Data
public class SpaceUsageAnalyzeResponse implements Serializable {

	/**
	 * 已使用大小
	 */
	private Long usedSize;

	/**
	 * 总大小
	 */
	private Long maxSize;

	/**
	 * 空间使用比例
	 */
	private Double sizeUsageRatio;

	/**
	 * 当前图片数量
	 */
	private Long usedCount;

	/**
	 * 最大图片数量
	 */
	private Long maxCount;

	/**
	 * 图片数量占比
	 */
	private Double countUsageRatio;

	private static final long serialVersionUID = 1L;
}
