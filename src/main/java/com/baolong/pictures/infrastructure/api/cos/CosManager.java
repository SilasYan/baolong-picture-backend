package com.baolong.pictures.infrastructure.api.cos;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 腾讯云对象存储服务
 * <p>
 * 叫做 XXXManager 主要是做区分, 表示这个类是可以单独抽取出去的
 */
@Slf4j
@Component
public class CosManager {

	@Resource
	private CosConfig cosConfig;

	@Resource
	private COSClient cosClient;

	/**
	 * 上传对象
	 *
	 * @param key  唯一键
	 * @param file 文件
	 */
	public PutObjectResult putObject(String key, File file) {
		PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, file);
		return cosClient.putObject(putObjectRequest);
	}

	/**
	 * 下载对象
	 *
	 * @param key 唯一键
	 */
	public COSObject getObject(String key) {
		GetObjectRequest getObjectRequest = new GetObjectRequest(cosConfig.getBucket(), key);
		return cosClient.getObject(getObjectRequest);
	}

	/**
	 * 删除对象
	 *
	 * @param key 文件 key
	 */
	public void deleteObject(String key) {
		cosClient.deleteObject(cosConfig.getBucket(), key);
	}

	/**
	 * 上传对象（数据万象）
	 * <p>
	 * <a href="https://cloud.tencent.com/document/product/436/115609">腾讯 COS 数据万象</a>
	 *
	 * @param key    唯一键
	 * @param file   文件
	 * @param openWx 开启数据万象; true: 开启; false: 关闭
	 */
	public PutObjectResult putPictureObject(String key, File file, boolean openWx) {
		PutObjectRequest putObjectRequest = new PutObjectRequest(cosConfig.getBucket(), key, file);
		// 对图片进行处理（获取基本信息也被视作为一种处理）
		PicOperations picOperations = new PicOperations();
		// 1 表示返回原图信息
		picOperations.setIsPicInfo(1);
		if (openWx) {
			// 规则处理
			List<PicOperations.Rule> rules = new ArrayList<>();
			// 处理图片格式为 webp 格式
			rules.add(ciFormatConversion(key, "webp"));
			// 针对大于 20KB 的图片, 需要生成缩略图
			if (file.length() > 2 * 1024) {
				rules.add(ciThumbnailConversion(key));
			}
			picOperations.setRules(rules);
		}
		putObjectRequest.setPicOperations(picOperations);
		return cosClient.putObject(putObjectRequest);
	}

	/**
	 * 数据万象: 转换图片格式
	 *
	 * @param imageKey        原图 key, 即图片的路径包含后缀
	 * @param targetImageType 目标图片类型; jpg、jpeg、png、webp、heif 更多参考文档
	 *                        <p>
	 *                        <a href="https://cloud.tencent.com/document/product/436/113299">转换图片格式文档</a>
	 */
	public PicOperations.Rule ciFormatConversion(String imageKey, String targetImageType) {
		PicOperations.Rule compressRule = new PicOperations.Rule();
		compressRule.setFileId(FileUtil.mainName(imageKey) + "." + targetImageType);
		compressRule.setBucket(cosConfig.getBucket());
		compressRule.setRule("imageMogr2/format/" + targetImageType);
		return compressRule;
	}

	/**
	 * 数据万象: 转换图片为缩略图（其实就是缩放）
	 *
	 * @param imageKey 原图 key, 即图片的路径包含后缀
	 *                 <p>
	 *                 <a href="https://cloud.tencent.com/document/product/436/113295">转换图片为缩略图文档</a>
	 */
	public PicOperations.Rule ciThumbnailConversion(String imageKey) {
		// 规则处理: 处理缩略图
		PicOperations.Rule thumbnailRule = new PicOperations.Rule();
		// 这里用的是原图的后缀, 不是 图片压缩转换格式的 webp 后缀, 因为这里是针对原图处理的
		thumbnailRule.setFileId(FileUtil.mainName(imageKey) + "_thumbnail." + FileUtil.getSuffix(imageKey));
		thumbnailRule.setBucket(cosConfig.getBucket());
		thumbnailRule.setRule(String.format("imageMogr2/thumbnail/%sx%s>", 256, 256));
		return thumbnailRule;
	}

	/**
	 * 获取图片主色调
	 *
	 * @param imageKey 图片 key
	 * @return 图片主色调
	 */
	public String getImageAve(String imageKey) {
		GetObjectRequest objectRequest = new GetObjectRequest(cosConfig.getBucket(), imageKey);
		// 设置图片处理规则为获取主色调
		objectRequest.putCustomQueryParameter("imageAve", null);
		// 获取对象
		COSObject cosObject = cosClient.getObject(objectRequest);
		try (
				COSObjectInputStream cosIp = cosObject.getObjectContent();
				ByteArrayOutputStream op = new ByteArrayOutputStream()
		) {
			// 读取流的内容
			byte[] bytes = new byte[1024];
			int len;
			while ((len = cosIp.read(bytes)) != -1) {
				op.write(bytes, 0, len);
			}
			// 将字节数组转换为字符串
			String aveColor = op.toString(StandardCharsets.UTF_8);
			return JSONUtil.parseObj(aveColor).getStr("RGB");
		} catch (Exception e) {
			log.error("获取图片主色调失败", e);
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取图片主色调失败");
		}
	}
}
