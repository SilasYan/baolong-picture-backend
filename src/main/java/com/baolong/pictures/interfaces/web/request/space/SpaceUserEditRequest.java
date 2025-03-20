package com.baolong.pictures.interfaces.web.request.space;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间用户编辑请求
 */
@Data
public class SpaceUserEditRequest implements Serializable {

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

	private static final long serialVersionUID = 1L;
}
