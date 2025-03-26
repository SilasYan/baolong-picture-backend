package com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片表
 *
 * @TableName picture
 */
@TableName(value = "picture")
@Data
public class PictureDO implements Serializable {

	/**
	 * 主键 ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 原图名称
	 */
	private String originName;

	/**
	 * 原图地址
	 */
	private String originUrl;

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
	 * 原图资源路径（存储服务器中路径）
	 */
	private String originPath;

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
	 * 压缩图资源路径
	 */
	private String compressPath;

	/**
	 * 缩略图地址
	 */
	private String thumbnailUrl;

	/**
	 * 缩略图资源路径
	 */
	private String thumbnailPath;

	/**
	 * 分类 ID
	 */
	private Long categoryId;

	/**
	 * 标签（逗号分隔的标签列表）
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
	 * 审核时间
	 */
	private Date reviewTime;

	/**
	 * 资源状态（0-存在 COS, 1-不存在 COS）
	 */
	private Integer resourceStatus;

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
	 * 是否分享（0-分享, 1-不分享）
	 */
	private Integer isShare;

	/**
	 * 扩图状态（0-普通图片, 1-扩图图片, 2-扩图成功后的图片）
	 */
	private Integer expandStatus;

	/**
	 * 是否删除
	 */
	private Integer isDelete;

	/**
	 * 编辑时间
	 */
	@TableField(value = "edit_time", fill = FieldFill.UPDATE)
	private Date editTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}
