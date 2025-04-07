package com.baolong.pictures.interfaces.web.space.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间编辑请求
 */
@Data
public class SpaceEditRequest implements Serializable {

	/**
	 * 空间ID
	 */
	private Long spaceId;

	/**
	 * 空间名称
	 */
	private String spaceName;

	private static final long serialVersionUID = 1L;
}
