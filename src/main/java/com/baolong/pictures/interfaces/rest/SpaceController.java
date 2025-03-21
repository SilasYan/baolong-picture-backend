package com.baolong.pictures.interfaces.rest;

import cn.hutool.core.util.ObjectUtil;
import com.baolong.pictures.application.service.SpaceApplicationService;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.interfaces.web.space.assembler.SpaceAssembler;
import com.baolong.pictures.interfaces.web.space.request.SpaceActivateRequest;
import com.baolong.pictures.interfaces.web.space.request.SpaceEditRequest;
import com.baolong.pictures.interfaces.web.space.request.SpaceQueryRequest;
import com.baolong.pictures.interfaces.web.space.request.SpaceUpdateRequest;
import com.baolong.pictures.interfaces.web.space.response.SpaceDetailVO;
import com.baolong.pictures.interfaces.web.space.response.SpaceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 空间表 (space) - 接口
 */
@RestController
@RequestMapping("/space")
@RequiredArgsConstructor
public class SpaceController {

	private final SpaceApplicationService spaceApplicationService;

	/**
	 * 激活空间
	 *
	 * @param spaceActivateRequest 空间激活请求
	 * @return 是否成功
	 */
	@PostMapping("/activate")
	public BaseResponse<Boolean> activateSpace(@RequestBody SpaceActivateRequest spaceActivateRequest) {
		ThrowUtils.throwIf(spaceActivateRequest == null, ErrorCode.PARAMS_ERROR);
		Space space = SpaceAssembler.toDomain(spaceActivateRequest);
		spaceApplicationService.activateSpace(space);
		return ResultUtils.success();
	}

	/**
	 * 编辑空间
	 *
	 * @param spaceEditRequest 空间编辑请求
	 * @return 是否成功
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editSpace(@RequestBody SpaceEditRequest spaceEditRequest) {
		ThrowUtils.throwIf(spaceEditRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceEditRequest.getSpaceId()), ErrorCode.PARAMS_ERROR);
		Space space = SpaceAssembler.toDomain(spaceEditRequest);
		spaceApplicationService.editSpace(space);
		return ResultUtils.success();
	}

	/**
	 * 获取登录用户的空间详情
	 *
	 * @return 空间详情
	 */
	@GetMapping("/loginUser/detail")
	public BaseResponse<SpaceDetailVO> getSpaceDetailByLoginUser() {
		Space space = spaceApplicationService.getSpaceDetailByLoginUser();
		return ResultUtils.success(SpaceAssembler.toSpaceDetailVO(space));
	}

	/**
	 * 删除空间
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
		Long id = deleteRequest.getId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(id), ErrorCode.PARAMS_ERROR);
		spaceApplicationService.deleteSpace(id);
		return ResultUtils.success();
	}

	/**
	 * 更新空间
	 *
	 * @param spaceUpdateRequest 空间更新请求
	 * @return 是否成功
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest) {
		ThrowUtils.throwIf(spaceUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceUpdateRequest.getSpaceId()), ErrorCode.PARAMS_ERROR);
		Space space = SpaceAssembler.toDomain(spaceUpdateRequest);
		spaceApplicationService.updateSpace(space);
		return ResultUtils.success();
	}

	/**
	 * 获取空间管理分页列表
	 *
	 * @param spaceQueryRequest 空间查询请求
	 * @return 空间管理分页列表
	 */
	@PostMapping("/manage/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<SpaceVO>> getSpacePageListAsManage(@RequestBody SpaceQueryRequest spaceQueryRequest) {
		PageVO<Space> spacePage = spaceApplicationService.getSpacePageListAsManage(SpaceAssembler.toDomain(spaceQueryRequest));
		return ResultUtils.success(SpaceAssembler.toSpaceVOPage(spacePage));
	}
}
