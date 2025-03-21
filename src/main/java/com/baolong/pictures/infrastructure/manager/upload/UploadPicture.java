package com.baolong.pictures.infrastructure.manager.upload;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.infrastructure.api.cos.CosManager;
import com.baolong.pictures.infrastructure.api.cos.CosConfig;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.manager.upload.picture.model.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 上传图片抽象
 *
 * @author Baolong 2025年03月08 14:20
 * @version 1.0
 * @since 1.8
 */
@Slf4j
public abstract class UploadPicture {

	@Resource
	protected CosManager cosManager;

	@Resource
	protected CosConfig cosConfig;

	/**
	 * 允许上传的图片格式
	 */
	final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp", "gif",
			"image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");

	/**
	 * 校验图片后缀
	 *
	 * @param suffix 文件后缀
	 * @return 是否匹配
	 */
	protected boolean checkPictureSuffix(String suffix) {
		if (StrUtil.isEmpty(suffix)) return false;
		return ALLOW_FORMAT_LIST.contains(suffix.toLowerCase());
	}

	/**
	 * 上传文件（执行流程）
	 *
	 * @param fileInputSource 文件输入源
	 * @param pathPrefix      路径前缀, 例如: images/100001/2025_03_08/
	 * @param openWx          开启数据万象; true: 开启; false: 关闭
	 * @return 返回对象
	 */
	public final UploadPictureResult uploadFile(Object fileInputSource, String pathPrefix, boolean openWx) {
		// 1. 校验文件
		this.validFile(fileInputSource);
		// 2. 获取文件后缀
		String fileSuffix = this.getFileSuffix(fileInputSource);
		// 3. 生成文件路径
		String uploadPath = String.format("%s%s.%s", pathPrefix, IdUtil.simpleUUID(), fileSuffix);
		File file = null;
		try {
			// 获取原图名称, 不包含后缀
			String originName = getFileNameWithoutSuffix(fileInputSource);
			// 4. 创建临时文件
			file = File.createTempFile(uploadPath, null);
			// 5. 处理文件输入源
			handleFile(fileInputSource, file);
			// 6. 上传文件到对象存储服务  Tips: 后续可以改为多种存储
			return uploadFileToStore(uploadPath, originName, file, openWx);
		} catch (Exception e) {
			log.error("文件上传到对象存储失败", e);
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
		} finally {
			// 7. 清理临时文件
			deleteTempFile(file);
		}
	}

	/**
	 * 校验文件输入源（文件/URL）
	 *
	 * @param fileInputSource 文件输入源
	 */
	protected abstract void validFile(Object fileInputSource);

	/**
	 * 处理文件输入源
	 *
	 * @param fileInputSource 文件输入源
	 * @param file            文件对象
	 */
	protected abstract void handleFile(Object fileInputSource, File file);

	/**
	 * 上传文件到存储服务
	 *
	 * @param uploadPath 上传路径
	 * @param originName 原文件名称
	 * @param file       文件对象
	 * @param openWx     开启数据万象; true: 开启; false: 关闭
	 * @return 图片上传结果
	 */
	protected UploadPictureResult uploadFileToStore(String uploadPath, String originName, File file, boolean openWx) {
		// 上传图片到对象存储
		PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file, openWx);
		// 图片原图信息
		ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
		if (openWx) {
			// 获取处理后的结果信息
			ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
			List<CIObject> objectList = processResults.getObjectList();
			if (CollUtil.isEmpty(objectList)) {
				return buildPictureResult(file, uploadPath, originName, imageInfo);
			}
			// 根据图片处理规则的顺序获取, 获取压缩图信息
			CIObject compressedCiObject = objectList.get(0);
			CIObject thumbnailCiObject = null;
			if (objectList.size() > 1) {
				// 获取压缩图信息
				thumbnailCiObject = objectList.get(1);
				return buildPictureResult(file, uploadPath, originName, imageInfo, compressedCiObject, thumbnailCiObject);
			} else {
				return buildPictureResult(file, uploadPath, originName, imageInfo, compressedCiObject);
			}
		} else {
			return buildPictureResult(file, uploadPath, originName, imageInfo);
		}
	}

	/**
	 * 获取文件后缀
	 *
	 * @param fileInputSource 文件输入源
	 * @return 文件后缀
	 */
	protected abstract String getFileSuffix(Object fileInputSource);

	/**
	 * 获取文件名称不包含后缀
	 *
	 * @param fileInputSource 文件输入源
	 * @return 文件名称不包含后缀
	 */
	protected abstract String getFileNameWithoutSuffix(Object fileInputSource);

	/**
	 * 删除临时文件
	 */
	public void deleteTempFile(File file) {
		if (file == null) {
			return;
		}
		boolean deleteResult = file.delete();
		if (!deleteResult) {
			log.error("临时文件删除失败, {}", file.getAbsolutePath());
		}
	}

	/**
	 * 构造图片上传结果
	 *
	 * @param file           文件对象
	 * @param uploadPath     上传路径
	 * @param originFilename 原始文件名, 不包含后缀
	 * @param imageInfo      图片信息
	 * @return 图片上传结果
	 */
	private UploadPictureResult buildPictureResult(File file, String uploadPath, String originFilename, ImageInfo imageInfo) {
		return buildPictureResult(file, uploadPath, originFilename, imageInfo, null, null);
	}

	/**
	 * 构造图片上传结果
	 *
	 * @param file               文件对象
	 * @param uploadPath         上传路径
	 * @param originFilename     原始文件名, 不包含后缀
	 * @param imageInfo          图片信息
	 * @param compressedCiObject 压缩图信息
	 * @return 图片上传结果
	 */
	private UploadPictureResult buildPictureResult(File file, String uploadPath, String originFilename,
												   ImageInfo imageInfo, CIObject compressedCiObject) {
		return buildPictureResult(file, uploadPath, originFilename, imageInfo, compressedCiObject, null);
	}

	/**
	 * 构造图片上传结果
	 *
	 * @param file               文件对象
	 * @param uploadPath         上传路径
	 * @param originFilename     原始文件名, 不包含后缀
	 * @param imageInfo          图片信息
	 * @param compressedCiObject 压缩图信息
	 * @param thumbnailCiObject  缩略图信息
	 * @return 图片上传结果
	 */
	private UploadPictureResult buildPictureResult(File file, String uploadPath, String originFilename
			, ImageInfo imageInfo, CIObject compressedCiObject, CIObject thumbnailCiObject) {
		String prefix = cosConfig.getHost() + "/";
		UploadPictureResult uploadPictureResult = new UploadPictureResult();
		uploadPictureResult.setOriginName(originFilename);
		String originUrl = prefix + uploadPath;
		uploadPictureResult.setOriginUrl(originUrl);
		long originSize = FileUtil.size(file);
		uploadPictureResult.setOriginSize(originSize);
		String originFormat = imageInfo.getFormat();
		uploadPictureResult.setOriginFormat(originFormat);
		int width = imageInfo.getWidth();
		uploadPictureResult.setOriginWidth(width);
		int height = imageInfo.getHeight();
		uploadPictureResult.setOriginHeight(height);
		uploadPictureResult.setOriginScale(NumberUtil.round(width * 1.0 / height, 2).doubleValue());
		uploadPictureResult.setOriginColor(cosManager.getImageAve(uploadPath));
		uploadPictureResult.setOriginPath(uploadPath);
		uploadPictureResult.setPicName(originFilename);
		// 压缩图数据填充
		if (compressedCiObject != null) {
			uploadPictureResult.setPicUrl(prefix + compressedCiObject.getKey());
			uploadPictureResult.setCompressSize(compressedCiObject.getSize().longValue());
			uploadPictureResult.setCompressFormat(compressedCiObject.getFormat());
			uploadPictureResult.setCompressPath(compressedCiObject.getKey());
		} else {
			uploadPictureResult.setPicUrl(originUrl);
			uploadPictureResult.setCompressSize(originSize);
			uploadPictureResult.setCompressFormat(originFormat);
			uploadPictureResult.setCompressPath(uploadPath);
		}
		// 缩略图数据填充
		if (thumbnailCiObject != null) {
			uploadPictureResult.setThumbnailUrl(prefix + thumbnailCiObject.getKey());
			uploadPictureResult.setThumbnailPath(thumbnailCiObject.getKey());
		} else {
			uploadPictureResult.setThumbnailUrl(originUrl);
			uploadPictureResult.setThumbnailPath(uploadPath);
		}
		uploadPictureResult.setResourceStatus(0);
		return uploadPictureResult;
	}
}
