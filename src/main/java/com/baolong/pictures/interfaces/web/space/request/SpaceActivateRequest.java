package com.baolong.pictures.interfaces.web.space.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间激活请求
 */
@Data
public class SpaceActivateRequest implements Serializable {

	/**
	 * 空间名称
	 */
	private String spaceName;

	private static final long serialVersionUID = 1L;
}
