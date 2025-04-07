package com.baolong.pictures.interfaces.web.space.request;

import com.baolong.pictures.infrastructure.common.page.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 空间查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SpaceQueryRequest extends PageRequest implements Serializable {

	/**
	 * 空间ID
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
	 * 空间级别（0-普通版, 1-专业版, 2-旗舰版）
	 */
	private Integer spaceLevel;

	/**
	 * 创建用户 ID
	 */
	private Long userId;

	private static final long serialVersionUID = 1L;
}
