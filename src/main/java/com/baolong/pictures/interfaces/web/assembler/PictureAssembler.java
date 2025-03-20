package com.baolong.pictures.interfaces.web.assembler;

import com.baolong.pictures.domain.picture.entity.Picture;
import com.baolong.pictures.interfaces.web.request.picture.PictureBatchEditRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureEditRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureReviewRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureUpdateRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureUploadRequest;
import com.baolong.pictures.interfaces.web.response.picture.PictureDetailVO;
import com.baolong.pictures.interfaces.web.response.picture.PictureHomeVO;
import com.baolong.pictures.interfaces.web.response.picture.PictureVO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 图片转换类
 *
 * @author Baolong 2025年03月05 20:50
 * @version 1.0
 * @since 1.8
 */
public class PictureAssembler {

	/**
	 * 图片上传请求 转为 图片实体
	 */
	public static Picture toPictureEntity(PictureUploadRequest pictureUploadRequest) {
		Picture picture = new Picture();
		if (pictureUploadRequest != null) {
			BeanUtils.copyProperties(pictureUploadRequest, picture);
		}
		return picture;
	}

	/**
	 * 图片更新请求 转为 图片实体
	 */
	public static Picture toPictureEntity(PictureUpdateRequest pictureUpdateRequest) {
		Picture picture = new Picture();
		if (pictureUpdateRequest != null) {
			BeanUtils.copyProperties(pictureUpdateRequest, picture);
		}
		return picture;
	}

	/**
	 * 图片编辑请求 转为 图片实体
	 */
	public static Picture toPictureEntity(PictureEditRequest pictureEditRequest) {
		Picture picture = new Picture();
		if (pictureEditRequest != null) {
			BeanUtils.copyProperties(pictureEditRequest, picture);
		}
		return picture;
	}

	/**
	 * 图片审核请求 转为 图片实体列表
	 */
	public static List<Picture> toPictureEntityList(PictureReviewRequest pictureReviewRequest) {
		List<Picture> pictures = new ArrayList<>();
		if (pictureReviewRequest != null) {
			if (pictureReviewRequest.getId() == null) {
				for (Long id : pictureReviewRequest.getIdList()) {
					Picture picture = new Picture();
					picture.setId(id);
					picture.setReviewStatus(pictureReviewRequest.getReviewStatus());
					picture.setReviewMessage(pictureReviewRequest.getReviewMessage());
					picture.setReviewTime(new Date());
					pictures.add(picture);
				}
			} else {
				Picture picture = new Picture();
				picture.setId(pictureReviewRequest.getId());
				picture.setReviewStatus(pictureReviewRequest.getReviewStatus());
				picture.setReviewMessage(pictureReviewRequest.getReviewMessage());
				picture.setReviewTime(new Date());
				pictures.add(picture);
			}
		}
		return pictures;
	}

	/**
	 * 图片批量编辑请求 转为 图片实体列表
	 */
	public static List<Picture> toPictureEntityList(PictureBatchEditRequest pictureBatchEditRequest) {
		List<Picture> pictures = new ArrayList<>();
		if (pictureBatchEditRequest != null) {
			for (Long id : pictureBatchEditRequest.getIdList()) {
				Picture picture = new Picture();
				picture.setId(id);
				picture.setCategoryId(pictureBatchEditRequest.getCategoryId());
				picture.setSpaceId(pictureBatchEditRequest.getSpaceId());
				pictures.add(picture);
				pictures.add(picture);
			}
		}
		return pictures;
	}

	/**
	 * 图片实体 转为 图片 VO
	 */
	public static PictureVO toPictureVO(Picture picture) {
		PictureVO pictureVO = new PictureVO();
		if (picture != null) {
			BeanUtils.copyProperties(picture, pictureVO);
		}
		return pictureVO;
	}

	/**
	 * 图片实体 转为 图片详情 VO
	 */
	public static PictureDetailVO toPictureDetailVO(Picture picture) {
		PictureDetailVO pictureDetailVO = new PictureDetailVO();
		if (picture != null) {
			BeanUtils.copyProperties(picture, pictureDetailVO);
		}
		return pictureDetailVO;
	}

	/**
	 * 图片实体 转为 图片首页 VO
	 */
	public static PictureHomeVO toPictureSimpleVO(Picture picture) {
		PictureHomeVO PictureHomeVO = new PictureHomeVO();
		if (picture != null) {
			BeanUtils.copyProperties(picture, PictureHomeVO);
		}
		return PictureHomeVO;
	}

	/**
	 * 图片首页 VO 转为 图片详情 VO
	 */
	public static PictureDetailVO toPictureDetailVO(PictureHomeVO PictureHomeVO) {
		PictureDetailVO pictureDetailVO = new PictureDetailVO();
		if (PictureHomeVO != null) {
			BeanUtils.copyProperties(PictureHomeVO, pictureDetailVO);
		}
		return pictureDetailVO;
	}
}
