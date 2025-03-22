package com.baolong.pictures.infrastructure.manager.upload.picture;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.manager.upload.UploadPicture;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 上传图片 URL
 *
 * @author Baolong 2025年03月08 16:08
 * @version 1.0
 * @since 1.8
 */
@Service
public class UploadPictureUrl extends UploadPicture {

	/**
	 * 校验文件输入源（文件/URL）
	 *
	 * @param fileInputSource 文件输入源
	 */
	@Override
	protected void validFile(Object fileInputSource) {
		String fileUrl = (String) fileInputSource;
		ThrowUtils.throwIf(StrUtil.isEmpty(fileUrl), ErrorCode.PARAMS_ERROR, "图片文件地址不能为空");
		// 校验 URL 格式
		try {
			new URL(fileUrl);
		} catch (MalformedURLException e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片文件地址错误");
		}
		// 校验 URL 协议
		if (!(fileUrl.startsWith("http://") || fileUrl.startsWith("https://"))) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "仅支持 HTTP 或 HTTPS 协议的文件地址");
		}
		// 校验 URL 是否存在
		try (HttpResponse response = HttpUtil.createRequest(Method.HEAD, fileUrl).execute()) {
			if (response.getStatus() != HttpStatus.HTTP_OK) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片文件地址不存在");
			}
			// 校验 URL 文件类型
			String contentType = response.header("Content-Type");
			if (StrUtil.isEmpty(contentType)) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片文件地址格式错误");
			}
			System.out.println(contentType);
			// 校验文件后缀
			ThrowUtils.throwIf(!checkPictureSuffix(contentType), ErrorCode.PARAMS_ERROR, "图片文件地址类型错误");
		}
	}

	/**
	 * 处理文件输入源
	 *
	 * @param fileInputSource 文件输入源
	 * @param file            文件对象
	 */
	@Override
	protected void handleFile(Object fileInputSource, File file) {
		String fileUrl = (String) fileInputSource;
		// 下载文件到临时目录
		HttpUtil.downloadFile(fileUrl, file);
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
		String fileUrl = (String) fileInputSource;
		return FileUtil.mainName(fileUrl).substring(0, 2) + "." + FileUtil.extName(fileUrl).split("&")[0];
	}

	/**
	 * 获取文件名称不包含后缀
	 *
	 * @param fileInputSource 文件输入源
	 * @return 文件名称不包含后缀
	 */
	@Override
	protected String getFileNameWithoutSuffix(Object fileInputSource) {
		return FileUtil.mainName((String) fileInputSource);
	}
}
