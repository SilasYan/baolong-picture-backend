package com.baolong.pictures.domain.picture.aggregate;

import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片交互领域模型
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class PictureInteraction extends PageRequest implements Serializable {

	/**
	 * 用户 ID
	 */
	private Long userId;

	/**
	 * 图片 ID
	 */
	private Long pictureId;

	/**
	 * 交互类型（0-点赞, 1-收藏）
	 */
	private Integer interactionType;

	/**
	 * 交互状态（0-存在, 1-取消）
	 */
	private Integer interactionStatus;

	/**
	 * 是否删除（0-正常, 1-删除）
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

	private static final long serialVersionUID = 1L;
}
