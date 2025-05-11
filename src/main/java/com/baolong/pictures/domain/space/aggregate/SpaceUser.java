package com.baolong.pictures.domain.space.aggregate;

import com.baolong.pictures.domain.user.aggregate.User;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 空间用户领域模型
 *
 * @TableName space_user
 */
@TableName(value = "space_user")
@Data
public class SpaceUser implements Serializable {

	// region 属性

	/**
	 * 空间用户ID
	 */
	private Long spaceUserId;

	/**
	 * 空间 ID
	 */
	private Long spaceId;

	/**
	 * 用户 ID
	 */
	private Long userId;

	/**
	 * 空间角色（CREATOR-创建者, EDITOR-编辑者, VIEWER-访问）
	 */
	private String spaceRole;

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
	 * 用户信息
	 */
	private User user;

	// endregion 属性

	// region 行为
	// endregion 行为

	private static final long serialVersionUID = 1L;
}
