package com.baolong.pictures.interfaces.web.response.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图片详情 VO
 */
@Data
public class PictureDetailVO implements Serializable {
	/**
	 * 图片 ID
	 */
	private Long id;

	/**
	 * 原图大小（单位: B）
	 */
	private Long originSize;

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
	 * 图片地址（展示, 压缩图地址）
	 */
	private String picUrl;

	/**
	 * 压缩图大小
	 */
	private Long compressSize;

	/**
	 * 压缩图格式
	 */
	private String compressFormat;

	/**
	 * 缩略图地址
	 */
	private String thumbnailUrl;

	/**
	 * 分类 ID
	 */
	private Long categoryId;

	/**
	 * 分类名称
	 */
	private String categoryName;

	/**
	 * 标签（逗号分隔的标签列表）
	 */
	private String tags;

	/**
	 * 标签名称列表
	 */
	private List<String> tagList;

	/**
	 * 查看数量
	 */
	private Integer viewQuantity;

	/**
	 * 点赞数量
	 */
	private Integer likeQuantity;

	/**
	 * 收藏数量
	 */
	private Integer collectQuantity;

	/**
	 * 下载数量
	 */
	private Integer downloadQuantity;

	/**
	 * 分享数量
	 */
	private Integer shareQuantity;

	/**
	 * 审核状态（0-待审核, 1-通过, 2-拒绝）
	 */
	private Integer reviewStatus;

	/**
	 * 创建用户 ID
	 */
	private Long userId;

	/**
	 * 创建用户昵称
	 */
	private String userName;

	/**
	 * 创建用户头像
	 */
	private String userAvatar;

	/**
	 * 所属空间 ID（0-表示公共空间）
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
	 * 是否分享（0-分享, 1-不分享）
	 */
	private Integer isShare;

	/**
	 * 登录用户是否点赞
	 */
	private Boolean loginUserIsLike = false;

	/**
	 * 登录用户是否收藏
	 */
	private Boolean loginUserIsCollect = false;

	/**
	 * 创建时间
	 */
	private Date createTime;

	private static final long serialVersionUID = 1L;
}
