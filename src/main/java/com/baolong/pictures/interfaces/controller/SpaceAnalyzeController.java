// package com.baolong.pictures.interfaces.controller;
//
// import com.baolong.pictures.domain.user.entity.TUser;
// import com.baolong.pictures.infrastructure.common.BaseResponse;
// import com.baolong.pictures.infrastructure.common.ResultUtils;
// import com.baolong.pictures.infrastructure.exception.ErrorCode;
// import com.baolong.pictures.infrastructure.exception.ThrowUtils;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceCategoryAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceRankAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceSizeAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceTagAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceUsageAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceUserAnalyzeRequest;
// import com.baolong.pictures.domain.space.entity.TeSpace;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceCategoryAnalyzeResponse;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceSizeAnalyzeResponse;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceTagAnalyzeResponse;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceUsageAnalyzeResponse;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceUserAnalyzeResponse;
// import com.baolong.pictures.application.service.SpaceAnalyzeApplicationService;
// import com.baolong.pictures.application.service.UserApplicationService;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// import javax.annotation.Resource;
// import javax.servlet.http.HttpServletRequest;
// import java.util.List;
//
// /**
//  * 空间分析控制层
//  *
//  * @author Baolong 2025年02月27 20:14
//  * @version 1.0
//  * @since 1.8
//  */
// @RestController
// @RequestMapping("/space/analyze")
// public class SpaceAnalyzeController {
//
// 	@Resource
// 	private SpaceAnalyzeApplicationService spaceAnalyzeApplicationService;
//
// 	@Resource
// 	private UserApplicationService userApplicationService;
//
// 	/**
// 	 * 空间使用分析
// 	 */
// 	@PostMapping("/usage")
// 	public BaseResponse<SpaceUsageAnalyzeResponse> getSpaceUsageAnalyze(
// 			@RequestBody SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest,
// 			HttpServletRequest request
// 	) {
// 		ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
// 		TUser loginTUser = userApplicationService.getLoginUser(request);
// 		SpaceUsageAnalyzeResponse spaceUsageAnalyze = spaceAnalyzeApplicationService.getSpaceUsageAnalyze(spaceUsageAnalyzeRequest, loginTUser);
// 		return ResultUtils.success(spaceUsageAnalyze);
// 	}
//
// 	/**
// 	 * 空间分类分析
// 	 */
// 	@PostMapping("/category")
// 	public BaseResponse<List<SpaceCategoryAnalyzeResponse>> getSpaceCategoryAnalyze(@RequestBody SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, HttpServletRequest request) {
// 		ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
// 		TUser loginTUser = userApplicationService.getLoginUser(request);
// 		List<SpaceCategoryAnalyzeResponse> resultList = spaceAnalyzeApplicationService.getSpaceCategoryAnalyze(spaceCategoryAnalyzeRequest, loginTUser);
// 		return ResultUtils.success(resultList);
// 	}
//
// 	/**
// 	 * 空间标签分析
// 	 */
// 	@PostMapping("/tag")
// 	public BaseResponse<List<SpaceTagAnalyzeResponse>> getSpaceTagAnalyze(@RequestBody SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, HttpServletRequest request) {
// 		ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
// 		TUser loginTUser = userApplicationService.getLoginUser(request);
// 		List<SpaceTagAnalyzeResponse> resultList = spaceAnalyzeApplicationService.getSpaceTagAnalyze(spaceTagAnalyzeRequest, loginTUser);
// 		return ResultUtils.success(resultList);
// 	}
//
// 	/**
// 	 * 空间大小分析
// 	 */
// 	@PostMapping("/size")
// 	public BaseResponse<List<SpaceSizeAnalyzeResponse>> getSpaceSizeAnalyze(@RequestBody SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, HttpServletRequest request) {
// 		ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
// 		TUser loginTUser = userApplicationService.getLoginUser(request);
// 		List<SpaceSizeAnalyzeResponse> resultList = spaceAnalyzeApplicationService.getSpaceSizeAnalyze(spaceSizeAnalyzeRequest, loginTUser);
// 		return ResultUtils.success(resultList);
// 	}
//
// 	/**
// 	 * 空间用户上传分析
// 	 */
// 	@PostMapping("/user")
// 	public BaseResponse<List<SpaceUserAnalyzeResponse>> getSpaceUserAnalyze(@RequestBody SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, HttpServletRequest request) {
// 		ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
// 		TUser loginTUser = userApplicationService.getLoginUser(request);
// 		List<SpaceUserAnalyzeResponse> resultList = spaceAnalyzeApplicationService.getSpaceUserAnalyze(spaceUserAnalyzeRequest, loginTUser);
// 		return ResultUtils.success(resultList);
// 	}
//
// 	/**
// 	 * 空间排行分析
// 	 */
// 	@PostMapping("/rank")
// 	public BaseResponse<List<TeSpace>> getSpaceRankAnalyze(@RequestBody SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, HttpServletRequest request) {
// 		ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
// 		TUser loginTUser = userApplicationService.getLoginUser(request);
// 		List<TeSpace> resultList = spaceAnalyzeApplicationService.getSpaceRankAnalyze(spaceRankAnalyzeRequest, loginTUser);
// 		return ResultUtils.success(resultList);
// 	}
//
// }
