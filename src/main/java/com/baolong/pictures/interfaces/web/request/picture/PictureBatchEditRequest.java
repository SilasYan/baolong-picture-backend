package com.baolong.pictures.interfaces.web.request.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片批量编辑请求
 */
@Data
public class PictureBatchEditRequest implements Serializable {

	/**
	 * 图片 ID 列表
	 */
	private List<Long> idList;

	/**
	 * 分类 ID
	 */
	private Long categoryId;

	/**
	 * 标签 ID 列表
	 */
	private List<Long> tagList;

	/**
	 * 输入的标签列表
	 */
	private List<String> inputTagList;

	/**
	 * 空间 ID
	 */
	private Long spaceId;

	/**
	 * 命名规则, 规则: 名称_{序号}
	 */
	private String nameRule;

	private static final long serialVersionUID = 1L;
}
