package com.baolong.pictures.interfaces.web.picture.assembler;

import com.baolong.pictures.domain.picture.aggregate.Picture;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.picture.request.PictureEditRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureExpandRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureGrabRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureQueryRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureReviewRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureSearchRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureUpdateRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureUploadRequest;
import com.baolong.pictures.interfaces.web.picture.response.PictureDetailVO;
import com.baolong.pictures.interfaces.web.picture.response.PictureHomeVO;
import com.baolong.pictures.interfaces.web.picture.response.PictureVO;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 图片转换类
 *
 * @author Baolong 2025年03月05 20:50
 * @version 1.0
 * @since 1.8
 */
public class PictureAssembler {

	/**
	 * 图片上传请求 转为 图片领域对象
	 */
	public static Picture toDomain(PictureUploadRequest pictureUploadRequest) {
		Picture picture = new Picture();
		BeanUtils.copyProperties(pictureUploadRequest, picture);
		return picture;
	}

	/**
	 * 图片更新请求 转为 图片领域对象
	 */
	public static Picture toDomain(PictureUpdateRequest pictureUpdateRequest) {
		Picture picture = new Picture();
		BeanUtils.copyProperties(pictureUpdateRequest, picture);
		return picture;
	}

	/**
	 * 图片编辑请求 转为 图片领域对象
	 */
	public static Picture toDomain(PictureEditRequest pictureEditRequest) {
		Picture picture = new Picture();
		BeanUtils.copyProperties(pictureEditRequest, picture);
		return picture;
	}

	/**
	 * 图片爬取请求 转为 图片领域对象
	 */
	public static Picture toDomain(PictureGrabRequest pictureGrabRequest) {
		Picture picture = new Picture();
		BeanUtils.copyProperties(pictureGrabRequest, picture);
		return picture;
	}

	/**
	 * 图片扩图请求 转为 图片领域对象
	 */
	public static Picture toDomain(PictureExpandRequest pictureExpandRequest) {
		Picture picture = new Picture();
		BeanUtils.copyProperties(pictureExpandRequest, picture);
		return picture;
	}

	/**
	 * 图片搜索请求 转为 图片领域对象
	 */
	public static Picture toDomain(PictureSearchRequest pictureSearchRequest) {
		Picture picture = new Picture();
		BeanUtils.copyProperties(pictureSearchRequest, picture);
		return picture;
	}

	/**
	 * 图片查询请求 转为 图片领域对象
	 */
	public static Picture toDomain(PictureQueryRequest pictureQueryRequest) {
		Picture picture = new Picture();
		BeanUtils.copyProperties(pictureQueryRequest, picture);
		return picture;
	}

	/**
	 * 图片审核请求 转为 图片领域对象列表
	 */
	public static List<Picture> toPictureEntityList(PictureReviewRequest pictureReviewRequest) {
		List<Picture> pictures = new ArrayList<>();
		if (pictureReviewRequest.getPictureId() == null) {
			for (Long id : pictureReviewRequest.getIdList()) {
				Picture picture = new Picture();
				picture.setPictureId(id);
				picture.setReviewStatus(pictureReviewRequest.getReviewStatus());
				picture.setReviewMessage(pictureReviewRequest.getReviewMessage());
				picture.setReviewTime(new Date());
				pictures.add(picture);
			}
		} else {
			Picture picture = new Picture();
			picture.setPictureId(pictureReviewRequest.getPictureId());
			picture.setReviewStatus(pictureReviewRequest.getReviewStatus());
			picture.setReviewMessage(pictureReviewRequest.getReviewMessage());
			picture.setReviewTime(new Date());
			pictures.add(picture);
		}
		return pictures;
	}

	/**
	 * 图片领域对象 转为 图片首页响应对象
	 */
	public static PictureHomeVO toPictureHomeVO(Picture picture) {
		PictureHomeVO PictureHomeVO = new PictureHomeVO();
		BeanUtils.copyProperties(picture, PictureHomeVO);
		return PictureHomeVO;
	}

	/**
	 * 图片首页响应对象 转为 图片详情响应对象
	 */
	public static PictureDetailVO toPictureDetailVO(PictureHomeVO PictureHomeVO) {
		PictureDetailVO pictureDetailVO = new PictureDetailVO();
		BeanUtils.copyProperties(PictureHomeVO, pictureDetailVO);
		return pictureDetailVO;
	}

	/**
	 * 图片领域对象 转为 图片详情响应对象
	 */
	public static PictureDetailVO toPictureDetailVO(Picture picture) {
		PictureDetailVO pictureDetailVO = new PictureDetailVO();
		BeanUtils.copyProperties(picture, pictureDetailVO);
		return pictureDetailVO;
	}

	/**
	 * 图片领域对象 转为 图片响应对象
	 */
	public static PictureVO toPictureVO(Picture picture) {
		PictureVO pictureVO = new PictureVO();
		BeanUtils.copyProperties(picture, pictureVO);
		return pictureVO;
	}

	/**
	 * 图片领域对象分页 转为 图片首页响应对象分页
	 */
	public static PageVO<PictureHomeVO> toPictureHomePageVO(PageVO<Picture> picturePageVO) {
		return new PageVO<>(picturePageVO.getCurrent()
				, picturePageVO.getPageSize()
				, picturePageVO.getTotal()
				, picturePageVO.getPages()
				, Optional.ofNullable(picturePageVO.getRecords())
				.orElse(List.of()).stream()
				.map(PictureAssembler::toPictureHomeVO)
				.collect(Collectors.toList())
		);
	}

	/**
	 * 图片领域对象分页 转为 图片响应对象分页
	 */
	public static PageVO<PictureVO> toPicturePageVO(PageVO<Picture> picturePageVO) {
		return new PageVO<>(picturePageVO.getCurrent()
				, picturePageVO.getPageSize()
				, picturePageVO.getTotal()
				, picturePageVO.getPages()
				, Optional.ofNullable(picturePageVO.getRecords())
				.orElse(List.of()).stream()
				.map(PictureAssembler::toPictureVO)
				.collect(Collectors.toList())
		);
	}
}
