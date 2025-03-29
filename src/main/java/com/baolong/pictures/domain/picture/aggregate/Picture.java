package com.baolong.pictures.domain.picture.aggregate;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.category.aggregate.Category;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureExpandStatusEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureReviewStatusEnum;
import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.common.constant.TextConstant;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 图片领域模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Picture extends PageRequest implements Serializable {

	// region 原始属性

	/**
	 * 图片 ID
	 */
	private Long pictureId;

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
	private Date editTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	/**
	 * 推荐综合得分
	 */
	private BigDecimal recommendScore;

	// endregion 原始属性

	// region 拓展属性

	/**
	 * 标签名称列表
	 */
	private List<String> tagList;

	/**
	 * 创建用户昵称
	 */
	private String userName;

	/**
	 * 创建用户头像
	 */
	private String userAvatar;

	/**
	 * 用户信息
	 */
	private User userInfo;

	/**
	 * 空间信息
	 */
	private Space spaceInfo;

	/**
	 * 空间名称
	 */
	private String spaceName;

	/**
	 * 空间类型（0-私有空间, 1-团队空间）
	 */
	private Integer spaceType;

	/**
	 * 图片地址
	 */
	private String pictureUrl;

	/**
	 * 搜索词（同时搜名称、简介等）
	 */
	private String searchText;

	/**
	 * 编辑时间[开始时间]
	 */
	private String startEditTime;

	/**
	 * 编辑时间[结束时间]
	 */
	private String endEditTime;

	/**
	 * 分类信息
	 */
	private Category categoryInfo;

	/**
	 * 分类名称
	 */
	private String categoryName;

	/**
	 * 爬取来源
	 */
	private String grabSource;

	/**
	 * 搜索来源
	 */
	private String searchSource;

	/**
	 * 搜索数量
	 */
	private Integer searchCount = 15;

	/**
	 * 关键词
	 */
	private String keyword;

	/**
	 * 名称前缀
	 */
	private String namePrefix;

	/**
	 * 爬取数量
	 */
	private Integer grabCount = 15;

	/**
	 * 随机种子, 应该大于 0 小于 100
	 */
	private Integer randomSeed;

	/**
	 * 登录用户是否点赞
	 */
	private Boolean loginUserIsLike = false;

	/**
	 * 登录用户是否收藏
	 */
	private Boolean loginUserIsCollect = false;

	/**
	 * 交互类型
	 */
	private Integer interactionType;

	/**
	 * 交互状态（0-存在, 1-取消）
	 */
	private Integer interactionStatus;

	/**
	 * 图片 ID 列表
	 */
	private List<Long> idList;

	/**
	 * 扩图类型（旋转、等比）
	 */
	private Integer expandType;

	/**
	 * 是否首页查询（默认为 false）
	 */
	private Boolean isHome = false;

	/**
	 * 是否查询扩图（默认为 false）
	 */
	private Boolean expandQuery = false;

	// endregion 拓展属性

	// region 领域方法

	/**
	 * 填充审核参数
	 *
	 * @param isAdmin      是否是管理员
	 * @param userId       用户ID
	 * @param spaceId      空间ID
	 * @param expandStatus 扩图状态（0-普通图片, 1-扩图图片, 2-扩图成功后的图片）
	 */
	public void fillReviewParams(boolean isAdmin, Long userId, Long spaceId, Integer expandStatus) {
		if ((ObjUtil.isNotEmpty(spaceId) && !spaceId.equals(0L))) {
			this.setReviewStatus(PictureReviewStatusEnum.PASS.getKey());
			this.setReviewMessage(TextConstant.REVIEW_AUTO_PASS_SPACE);
			this.setReviewerUser(userId);
			this.setReviewTime(new Date());
			return;
		}
		if (ObjUtil.isNotEmpty(expandStatus) && PictureExpandStatusEnum.YES_SUCCESS.getKey().equals(expandStatus)) {
			this.setReviewStatus(PictureReviewStatusEnum.PASS.getKey());
			this.setReviewMessage(TextConstant.REVIEW_AUTO_PASS_EXPAND);
			this.setReviewerUser(userId);
			this.setReviewTime(new Date());
			return;
		}
		if (isAdmin) {
			this.setReviewStatus(PictureReviewStatusEnum.PASS.getKey());
			this.setReviewMessage(TextConstant.REVIEW_AUTO_PASS_ADMIN);
			this.setReviewerUser(userId);
			this.setReviewTime(new Date());
			return;
		}
		this.setReviewStatus(PictureReviewStatusEnum.REVIEWING.getKey());
	}

	/**
	 * 校验图片更新
	 */
	public void validPictureUpdateAndEdit() {
		ThrowUtils.throwIf(ObjUtil.isNull(this.getPictureId()), ErrorCode.PARAMS_ERROR, "图片 ID 不能为空");
		ThrowUtils.throwIf(StrUtil.isEmpty(this.getPicName()), ErrorCode.PARAMS_ERROR, "图片名称不能为空");
		ThrowUtils.throwIf(this.getPicName().length() > 100, ErrorCode.PARAMS_ERROR, "图片名称过长");
		if (StrUtil.isNotEmpty(this.getPicDesc())) {
			ThrowUtils.throwIf(this.getPicDesc().length() > 500, ErrorCode.PARAMS_ERROR, "图片介绍过长");
		}
	}

	// endregion 领域方法

	private static final long serialVersionUID = 1L;
}
