package com.baolong.pictures.interfaces.rest;

import cn.hutool.core.util.ObjectUtil;
import com.baolong.pictures.application.service.SpaceApplicationService;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.interfaces.web.request.space.SpaceActivateRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceAddRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceEditRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceQueryRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceUpdateRequest;
import com.baolong.pictures.interfaces.web.response.space.SpaceDetailVO;
import com.baolong.pictures.interfaces.web.response.space.SpaceLevelVO;
import com.baolong.pictures.interfaces.web.response.space.SpaceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 空间接口
 */
@RestController
@RequestMapping("/space")
@RequiredArgsConstructor
public class SpaceController {

	private final SpaceApplicationService spaceApplicationService;

	// region 增删改相关

	/**
	 * 激活空间
	 */
	@PostMapping("/activate")
	public BaseResponse<Long> activateSpace(@RequestBody SpaceActivateRequest spaceActivateRequest) {
		ThrowUtils.throwIf(spaceActivateRequest == null, ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(spaceApplicationService.activateSpace(spaceActivateRequest));
	}

	/**
	 * 新增空间
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addSpace(@RequestBody SpaceAddRequest spaceAddRequest) {
		ThrowUtils.throwIf(spaceAddRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceAddRequest.getUserId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(spaceApplicationService.addSpace(spaceAddRequest));
	}

	/**
	 * 删除空间
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(deleteRequest.getId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(spaceApplicationService.deleteSpace(deleteRequest));
	}

	/**
	 * 更新空间
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
		ThrowUtils.throwIf(spaceUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceUpdateRequest.getId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(spaceApplicationService.updateSpace(spaceUpdateRequest));
	}

	/**
	 * 编辑空间
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest) {
		ThrowUtils.throwIf(spaceEditRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceEditRequest.getId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(spaceApplicationService.editSpace(spaceEditRequest));
	}

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 获取登录用户的空间详情
	 */
	@GetMapping("/loginUser/detail")
	public BaseResponse<SpaceDetailVO> getSpaceDetailByLoginUser() {
		return ResultUtils.success(spaceApplicationService.getSpaceDetailByLoginUser());
	}

	/**
	 * 根据空间 ID 获取空间详情
	 */
	@GetMapping("/detail")
	public BaseResponse<SpaceDetailVO> getSpaceDetailById(Long id) {
		ThrowUtils.throwIf(ObjectUtil.isEmpty(id), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(spaceApplicationService.getSpaceVOById(id));
	}

	/**
	 * 获取用户空间列表
	 */
	@GetMapping("/list")
	public BaseResponse<List<SpaceDetailVO>> getSpaceListAsUser(SpaceQueryRequest spaceQueryRequest) {
		return ResultUtils.success(spaceApplicationService.getSpaceListAsUser(spaceQueryRequest));
	}

	/**
	 * 获取空间管理分页列表
	 */
	@PostMapping("/manage/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<SpaceVO>> getSpacePageListAsManage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
		return ResultUtils.success(spaceApplicationService.getSpacePageListAsManage(spaceQueryRequest));
	}

	/**
	 * 获取空间等级列表
	 */
	@GetMapping("/level")
	public BaseResponse<List<SpaceLevelVO>> getSpaceLevelList() {

		return ResultUtils.success(spaceApplicationService.getSpaceLevelList());
	}

	// endregion 查询相关
}
