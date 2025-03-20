package com.baolong.pictures.interfaces.vo.space;

import com.baolong.pictures.interfaces.vo.user.UserDetailVO;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 空间用户 VO
 */
@Data
public class SpaceUserVO implements Serializable {

	/**
	 * 空间用户 ID
	 */
	private Long id;

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
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 用户信息
	 */
	private UserDetailVO user;

	private static final long serialVersionUID = 1L;
}
