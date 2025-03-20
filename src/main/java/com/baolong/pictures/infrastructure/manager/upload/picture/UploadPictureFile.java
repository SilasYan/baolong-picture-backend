package com.baolong.pictures.infrastructure.manager.upload.picture;

import cn.hutool.core.io.FileUtil;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.manager.upload.UploadPicture;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 上传图片文件
 *
 * @author Baolong 2025年03月08 14:20
 * @version 1.0
 * @since 1.8
 */
@Service
public class UploadPictureFile extends UploadPicture {

	/**
	 * 校验文件输入源（文件）
	 *
	 * @param fileInputSource 文件输入源
	 */
	@Override
	protected void validFile(Object fileInputSource) {
		MultipartFile multipartFile = (MultipartFile) fileInputSource;
		ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR, "图片文件不能为空");
		// 获取文件后缀
		String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
		// 校验文件后缀
		ThrowUtils.throwIf(!checkPictureSuffix(fileSuffix), ErrorCode.PARAMS_ERROR, "图片文件类型错误");
	}

	/**
	 * 处理文件输入源
	 *
	 * @param fileInputSource 文件输入源
	 * @param file            文件对象
	 */
	@Override
	protected void handleFile(Object fileInputSource, File file) {
		MultipartFile multipartFile = (MultipartFile) fileInputSource;
		try {
			multipartFile.transferTo(file);
		} catch (IOException e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片文件处理失败");
		}
	}

	// /**
	//  * 上传文件到存储服务
	//  *
	//  * @param uploadPath 上传路径
	//  * @param file       文件对象
	//  */
	// @Override
	// protected Object uploadFileToStore(String uploadPath, File file) {
	// 	// 获取原图名称, 不包含后缀
	// 	String originName = getFileNameWithoutSuffix(file);
	// 	// 上传图片到对象存储
	// 	PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
	// 	// 图片原图信息
	// 	ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
	// 	// 获取处理后的结果信息
	// 	ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
	// 	List<CIObject> objectList = processResults.getObjectList();
	// 	if (CollUtil.isEmpty(objectList)) {
	// 		return buildPictureResult(file, uploadPath, originName, imageInfo);
	// 	}
	// 	// 根据图片处理规则的顺序获取, 获取压缩图信息
	// 	CIObject compressedCiObject = objectList.get(0);
	// 	CIObject thumbnailCiObject = null;
	// 	if (objectList.size() > 1) {
	// 		// 获取压缩图信息
	// 		thumbnailCiObject = objectList.get(1);
	// 		return buildPictureResult(file, uploadPath, originName, imageInfo, compressedCiObject, thumbnailCiObject);
	// 	} else {
	// 		return buildPictureResult(file, uploadPath, originName, imageInfo, compressedCiObject);
	// 	}
	// }

	/**
	 * 获取文件后缀
	 *
	 * @param fileInputSource 文件输入源
	 * @return 文件后缀
	 */
	@Override
	protected String getFileSuffix(Object fileInputSource) {
		MultipartFile multipartFile = (MultipartFile) fileInputSource;
		return FileUtil.getSuffix(multipartFile.getOriginalFilename());
	}

	/**
	 * 获取文件名称不包含后缀
	 *
	 * @param fileInputSource 文件输入源
	 * @return 文件名称不包含后缀
	 */
	@Override
	protected String getFileNameWithoutSuffix(Object fileInputSource) {
		MultipartFile multipartFile = (MultipartFile) fileInputSource;
		return FileUtil.mainName(multipartFile.getOriginalFilename());
	}
}
