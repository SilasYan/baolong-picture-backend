package com.baolong.pictures.infrastructure.manager.upload.picture.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 图片上传结果
 *
 * @author Baolong 2025年03月08 14:59
 * @version 1.0
 * @since 1.8
 */
@Data
@Accessors(chain = true)
public class UploadPictureResult {

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
	 * 资源状态（0-存在 COS, 1-不存在 COS）
	 */
	private Integer resourceStatus;

}
