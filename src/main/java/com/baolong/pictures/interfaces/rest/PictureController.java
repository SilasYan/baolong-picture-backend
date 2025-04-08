package com.baolong.pictures.interfaces.rest;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.application.service.PictureApplicationService;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.picture.aggregate.Picture;
import com.baolong.pictures.domain.picture.aggregate.PictureInteraction;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureInteractionStatusEnum;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureInteractionTypeEnum;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.infrastructure.api.bailian.model.BaiLianTaskResponse;
import com.baolong.pictures.infrastructure.api.bailian.model.CreateBaiLianTaskResponse;
import com.baolong.pictures.infrastructure.api.grab.model.GrabPictureResult;
import com.baolong.pictures.infrastructure.api.pictureSearch.enums.SearchSourceEnum;
import com.baolong.pictures.infrastructure.api.pictureSearch.model.SearchPictureResult;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.function.limit.annotation.Limit;
import com.baolong.pictures.infrastructure.function.limit.enums.LimitType;
import com.baolong.pictures.interfaces.web.picture.assembler.PictureAssembler;
import com.baolong.pictures.interfaces.web.picture.assembler.PictureInteractionAssembler;
import com.baolong.pictures.interfaces.web.picture.request.PictureEditRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureExpandRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureGrabRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureInteractionRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureQueryRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureReviewRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureSearchRequest;
import com.baolong.pictures.interfaces.web.picture.request.PictureUploadRequest;
import com.baolong.pictures.interfaces.web.picture.response.PictureDetailVO;
import com.baolong.pictures.interfaces.web.picture.response.PictureHomeVO;
import com.baolong.pictures.interfaces.web.picture.response.PictureVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 图片表 (picture) - 接口
 *
 * @author Baolong 2025年03月20 20:42
 * @version 1.0
 * @since 1.8
 */
@RestController
@RequestMapping("/picture")
@RequiredArgsConstructor
public class PictureController {

	private final PictureApplicationService pictureApplicationService;

	/**
	 * 上传图片（文件）
	 *
	 * @param multipartFile        图片文件
	 * @param pictureUploadRequest 图片上传请求
	 * @return 图片详情
	 */
	@PostMapping("/upload/file")
	public BaseResponse<PictureDetailVO> uploadPictureByFile(@RequestPart("file") MultipartFile multipartFile,
															 PictureUploadRequest pictureUploadRequest) {
		ThrowUtils.throwIf(multipartFile == null, ErrorCode.PARAMS_ERROR);
		Picture picture = PictureAssembler.toDomain(pictureUploadRequest);
		Picture newPicture = pictureApplicationService.uploadPicture(multipartFile, picture);
		return ResultUtils.success(PictureAssembler.toPictureDetailVO(newPicture));
	}

	/**
	 * 上传图片（URL）
	 *
	 * @param pictureUploadRequest 图片上传请求
	 * @return 图片详情
	 */
	@PostMapping("/upload/url")
	public BaseResponse<PictureDetailVO> uploadPictureByUrl(@RequestBody PictureUploadRequest pictureUploadRequest) {
		String pictureUrl = pictureUploadRequest.getPictureUrl();
		ThrowUtils.throwIf(StrUtil.isEmpty(pictureUrl), ErrorCode.PARAMS_ERROR);
		Picture picture = PictureAssembler.toDomain(pictureUploadRequest);
		Picture newPicture = pictureApplicationService.uploadPicture(pictureUrl, picture);
		return ResultUtils.success(PictureAssembler.toPictureDetailVO(newPicture));
	}

	/**
	 * 删除图片
	 *
	 * @param deleteRequest 图片删除请求
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjUtil.isEmpty(deleteRequest.getId()), ErrorCode.PARAMS_ERROR);
		pictureApplicationService.deletePicture(deleteRequest.getId());
		return ResultUtils.success();
	}

	/**
	 * 编辑图片
	 *
	 * @param pictureEditRequest 图片编辑请求
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest) {
		ThrowUtils.throwIf(pictureEditRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjUtil.isEmpty(pictureEditRequest.getPictureId()), ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(StrUtil.isEmpty(pictureEditRequest.getPicName()), ErrorCode.PARAMS_ERROR, "图片名称不能为空");
		ThrowUtils.throwIf(pictureEditRequest.getPicName().length() > 100, ErrorCode.PARAMS_ERROR, "图片名称过长");
		if (StrUtil.isNotEmpty(pictureEditRequest.getPicDesc())) {
			ThrowUtils.throwIf(pictureEditRequest.getPicDesc().length() > 500, ErrorCode.PARAMS_ERROR, "图片介绍过长");
		}
		Picture picture = PictureAssembler.toDomain(pictureEditRequest);
		pictureApplicationService.editPicture(picture);
		return ResultUtils.success();
	}

	/**
	 * 根据图片ID获取图片详情
	 *
	 * @param pictureId 图片ID
	 * @return 图片详情
	 */
	@Limit(key = "PictureDetail:", time = 1, count = 1, limitType = LimitType.IP, errMsg = "请求太频繁!")
	@GetMapping("/detail")
	public BaseResponse<PictureDetailVO> getPictureDetailById(Long pictureId) {
		ThrowUtils.throwIf(ObjectUtil.isEmpty(pictureId), ErrorCode.PARAMS_ERROR);
		Picture picture = pictureApplicationService.getPictureDetailById(pictureId);
		PictureDetailVO pictureDetailVO;
		// 如果当前用户没登录则只获取简单字段即可
		if (StpUtil.isLogin()) {
			pictureDetailVO = PictureAssembler.toPictureDetailVO(picture);
		} else {
			PictureHomeVO PictureHomeVO = PictureAssembler.toPictureHomeVO(picture);
			pictureDetailVO = PictureAssembler.toPictureDetailVO(PictureHomeVO);
		}
		return ResultUtils.success(pictureDetailVO);
	}

	/**
	 * 获取首页图片列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 首页图片列表
	 */
	@GetMapping("/home/list")
	public BaseResponse<PageVO<PictureHomeVO>> getPicturePageListAsHome(PictureQueryRequest pictureQueryRequest) {
		ThrowUtils.throwIf(pictureQueryRequest.getPageSize() > 20, ErrorCode.PARAMS_ERROR);
		Picture picture = PictureAssembler.toDomain(pictureQueryRequest);
		PageVO<Picture> picturePageVO = pictureApplicationService.getPicturePageListAsHome(picture);
		return ResultUtils.success(PictureAssembler.toPictureHomePageVO(picturePageVO));
	}

	/**
	 * 获取个人空间图片分页列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 个人空间图片分页列表
	 */
	@PostMapping("/personSpace/page")
	public BaseResponse<PageVO<PictureVO>> getPicturePageListAsPersonSpace(@RequestBody PictureQueryRequest pictureQueryRequest) {
		Picture picture = PictureAssembler.toDomain(pictureQueryRequest);
		PageVO<Picture> picturePageVO = pictureApplicationService.getPicturePageListAsPersonSpace(picture);
		return ResultUtils.success(PictureAssembler.toPicturePageVO(picturePageVO));
	}

	/**
	 * 获取团队空间图片分页列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 团队空间图片分页列表
	 */
	@PostMapping("/teamSpace/page")
	public BaseResponse<PageVO<PictureVO>> getPicturePageListAsTeamSpace(@RequestBody PictureQueryRequest pictureQueryRequest) {
		Picture picture = PictureAssembler.toDomain(pictureQueryRequest);
		PageVO<Picture> picturePageVO = pictureApplicationService.getPicturePageListAsTeamSpace(picture);
		return ResultUtils.success(PictureAssembler.toPicturePageVO(picturePageVO));
	}

	/**
	 * 获取个人发布的图片分页列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 个人发布的图片分页列表
	 */
	@PostMapping("/personRelease/page")
	public BaseResponse<PageVO<PictureVO>> getPicturePageListAsPersonRelease(@RequestBody PictureQueryRequest pictureQueryRequest) {
		Picture picture = PictureAssembler.toDomain(pictureQueryRequest);
		PageVO<Picture> picturePageVO = pictureApplicationService.getPicturePageListAsPersonRelease(picture);
		return ResultUtils.success(PictureAssembler.toPicturePageVO(picturePageVO));
	}

	/**
	 * 图片下载
	 *
	 * @param pictureInteractionRequest 图片互动请求
	 * @return 原图地址
	 */
	@Limit(key = "PictureDownload:", count = 1, limitType = LimitType.IP, errMsg = "图片下载太频繁，请稍后再试!")
	@PostMapping("/download")
	public BaseResponse<String> pictureDownload(@RequestBody PictureInteractionRequest pictureInteractionRequest) {
		ThrowUtils.throwIf(pictureInteractionRequest == null, ErrorCode.PARAMS_ERROR);
		Long pictureId = pictureInteractionRequest.getPictureId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(pictureId), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(pictureApplicationService.pictureDownload(pictureId));
	}

	/**
	 * 图片分享
	 *
	 * @param pictureInteractionRequest 图片互动请求
	 */
	@Limit(key = "PictureShare:", time = 5, count = 1, limitType = LimitType.IP, errMsg = "图片分享太频繁，请稍后再试!")
	@PostMapping("/share")
	public BaseResponse<Boolean> pictureShare(@RequestBody PictureInteractionRequest pictureInteractionRequest) {
		ThrowUtils.throwIf(pictureInteractionRequest == null, ErrorCode.PARAMS_ERROR);
		Long pictureId = pictureInteractionRequest.getPictureId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(pictureId), ErrorCode.PARAMS_ERROR);
		pictureApplicationService.pictureShare(pictureId);
		return ResultUtils.success();
	}

	/**
	 * 图片点赞
	 *
	 * @param pictureInteractionRequest 图片互动请求
	 */
	@Limit(key = "PictureLike:", time = 5, count = 2, limitType = LimitType.IP, errMsg = "图片点赞太频繁，请稍后再试!")
	@PostMapping("/interaction")
	public BaseResponse<Boolean> pictureLikeOrCollect(@RequestBody PictureInteractionRequest pictureInteractionRequest) {
		ThrowUtils.throwIf(pictureInteractionRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(pictureInteractionRequest.getPictureId()), ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(pictureInteractionRequest.getInteractionType()), ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(pictureInteractionRequest.getInteractionStatus()), ErrorCode.PARAMS_ERROR);
		Integer interactionType = pictureInteractionRequest.getInteractionType();
		if (!PictureInteractionTypeEnum.LIKE.getKey().equals(interactionType)
				&& !PictureInteractionTypeEnum.COLLECT.getKey().equals(interactionType)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片互动类型错误!");
		}
		Integer interactionStatus = pictureInteractionRequest.getInteractionStatus();
		if (!PictureInteractionStatusEnum.keys().contains(interactionStatus)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片互动状态错误!");
		}
		PictureInteraction pictureInteraction = PictureInteractionAssembler.toDomain(pictureInteractionRequest);
		pictureApplicationService.pictureLikeOrCollect(pictureInteraction);
		return ResultUtils.success();
	}

	/**
	 * 审核图片（包含批量审核）
	 *
	 * @param pictureReviewRequest 图片审核请求
	 */
	@PostMapping("/review")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> reviewPicture(@RequestBody PictureReviewRequest pictureReviewRequest) {
		ThrowUtils.throwIf(pictureReviewRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(pictureReviewRequest.getPictureId() == null
				&& CollUtil.isEmpty(pictureReviewRequest.getIdList()), ErrorCode.PARAMS_ERROR);
		List<Picture> pictureList = PictureAssembler.toPictureEntityList(pictureReviewRequest);
		pictureApplicationService.reviewPicture(pictureList);
		return ResultUtils.success();
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 图片管理分页列表
	 */
	@PostMapping("/manage/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<PictureVO>> getPicturePageListAsManage(@RequestBody PictureQueryRequest pictureQueryRequest) {
		Picture picture = PictureAssembler.toDomain(pictureQueryRequest);
		PageVO<Picture> picturePageVO = pictureApplicationService.getPicturePageListAsManage(picture);
		return ResultUtils.success(PictureAssembler.toPicturePageVO(picturePageVO));
	}

	/**
	 * 爬取图片
	 *
	 * @param pictureGrabRequest 图片爬取请求
	 * @return 爬取结果
	 */
	@PostMapping("/grab")
	public BaseResponse<List<GrabPictureResult>> grabPicture(@RequestBody PictureGrabRequest pictureGrabRequest) {
		ThrowUtils.throwIf(pictureGrabRequest == null, ErrorCode.PARAMS_ERROR);
		String keyword = pictureGrabRequest.getKeyword();
		if (StrUtil.isEmpty(keyword)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "关键词不能为空");
		}
		Picture picture = PictureAssembler.toDomain(pictureGrabRequest);
		return ResultUtils.success(pictureApplicationService.grabPicture(picture));
	}

	/**
	 * 上传爬取图片
	 *
	 * @param pictureUploadRequest 图片上传请求
	 * @return 上传结果
	 */
	@PostMapping("/upload/grab")
	public BaseResponse<Boolean> uploadPictureByGrab(@RequestBody PictureUploadRequest pictureUploadRequest) {
		ThrowUtils.throwIf(pictureUploadRequest == null, ErrorCode.PARAMS_ERROR);
		if (StrUtil.isEmpty(pictureUploadRequest.getPictureUrl())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片链接不能为空");
		}
		Picture picture = PictureAssembler.toDomain(pictureUploadRequest);
		pictureApplicationService.uploadPictureByGrab(picture);
		return ResultUtils.success();
	}

	/**
	 * 以图搜图
	 *
	 * @param pictureSearchRequest 以图搜图请求
	 * @return 搜图的图片列表
	 */
	@PostMapping("/search")
	public BaseResponse<List<SearchPictureResult>> searchPicture(@RequestBody PictureSearchRequest pictureSearchRequest) {
		ThrowUtils.throwIf(pictureSearchRequest == null, ErrorCode.PARAMS_ERROR);
		if (ObjUtil.isEmpty(pictureSearchRequest.getPictureId())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "图片ID不能为空");
		}
		Picture picture = PictureAssembler.toDomain(pictureSearchRequest);
		if (!SearchSourceEnum.keys().contains(picture.getSearchSource())) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "不支持的搜索源");
		}
		return ResultUtils.success(pictureApplicationService.searchPicture(picture));
	}

	/**
	 * 扩图
	 *
	 * @param pictureExpandRequest 图片扩展请求
	 * @return 扩图任务结果
	 */
	@PostMapping("/expand")
	public BaseResponse<CreateBaiLianTaskResponse> expandPicture(@RequestBody PictureExpandRequest pictureExpandRequest) {
		ThrowUtils.throwIf(pictureExpandRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(StrUtil.isEmpty(pictureExpandRequest.getPicUrl()), ErrorCode.PARAMS_ERROR, "图片地址不能为空");
		ThrowUtils.throwIf(ObjectUtil.isEmpty(pictureExpandRequest.getExpandType()), ErrorCode.PARAMS_ERROR, "扩图类型错误");
		Picture picture = PictureAssembler.toDomain(pictureExpandRequest);
		return ResultUtils.success(pictureApplicationService.expandPicture(picture));
	}

	/**
	 * 扩图查询
	 *
	 * @param taskId 任务ID
	 * @return 扩图任务结果
	 */
	@GetMapping("/expand/query")
	public BaseResponse<BaiLianTaskResponse> expandPictureQuery(String taskId) {
		ThrowUtils.throwIf(StrUtil.isEmpty(taskId), ErrorCode.PARAMS_ERROR, "任务ID不能为空");
		return ResultUtils.success(pictureApplicationService.expandPictureQuery(taskId));
	}
}
