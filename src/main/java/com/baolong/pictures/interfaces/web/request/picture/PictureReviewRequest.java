package com.baolong.pictures.interfaces.web.request.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片审核请求
 */
@Data
public class PictureReviewRequest implements Serializable {

	/**
	 * 图片 ID
	 */
	private Long id;

	/**
	 * 图片 ID 列表
	 */
	private List<Long> idList;

	/**
	 * 审核状态（0-待审核, 1-通过, 2-拒绝）
	 */
	private Integer reviewStatus;

	/**
	 * 审核信息
	 */
	private String reviewMessage;

	private static final long serialVersionUID = 1L;
}
