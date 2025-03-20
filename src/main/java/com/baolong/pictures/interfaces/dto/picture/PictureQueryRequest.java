package com.baolong.pictures.interfaces.dto.picture;

import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PictureQueryRequest extends PageRequest implements Serializable {

	/**
	 * 搜索词（同时搜名称、简介等）
	 */
	private String searchText;

	/**
	 * 图片 ID
	 */
	private Long id;

	/**
	 * 原图格式
	 */
	private String originFormat;

	/**
	 * 原图宽度
	 */
	private Integer originWidth;

	/**
	 * 原图高度
	 */
	private Integer originHeight;

	/**
	 * 原图比例（宽高比）
	 */
	private Double originScale;

	/**
	 * 原图主色调
	 */
	private String originColor;

	/**
	 * 图片名称（展示）
	 */
	private String picName;

	/**
	 * 图片描述（展示）
	 */
	private String picDesc;

	/**
	 * 分类 ID
	 */
	private Long categoryId;

	/**
	 * 标签（只能传一个）
	 */
	private String tags;

	/**
	 * 创建用户 ID
	 */
	private Long userId;

	/**
	 * 所属空间 ID（0-表示公共空间）
	 */
	private Long spaceId;

	/**
	 * 审核状态（0-待审核, 1-通过, 2-拒绝）
	 */
	private Integer reviewStatus;

	/**
	 * 审核信息
	 */
	private String reviewMessage;

	/**
	 * 审核人 ID
	 */
	private Long reviewerUser;

	/**
	 * 上传时间[开始时间]
	 */
	private Date startCreateTime;

	/**
	 * 上传时间[结束时间]
	 */
	private Date endCreateTime;

	/**
	 * 编辑时间[开始时间]
	 */
	private Date startEditTime;

	/**
	 * 编辑时间[结束时间]
	 */
	private Date endEditTime;

	private static final long serialVersionUID = 1L;
}
