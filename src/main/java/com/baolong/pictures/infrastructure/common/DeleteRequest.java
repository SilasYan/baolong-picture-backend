package com.baolong.pictures.infrastructure.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 删除请求
 */
@Data
public class DeleteRequest implements Serializable {

	/**
	 * ID
	 */
	private Long id;

	private static final long serialVersionUID = 1L;
}
