package com.baolong.pictures.interfaces.dto.space;

import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 空间用户查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceUserQueryRequest extends PageRequest implements Serializable {

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
