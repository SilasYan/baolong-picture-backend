package com.baolong.pictures.interfaces.rest;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.application.service.SpaceUserApplicationService;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.interfaces.web.request.space.SpaceUserAddRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceUserEditRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceUserQueryRequest;
import com.baolong.pictures.interfaces.web.response.space.SpaceUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 空间用户接口
 */
@RestController
@RequestMapping("/spaceUser")
@RequiredArgsConstructor
public class SpaceUserController {

	private final SpaceUserApplicationService spaceUserApplicationService;

	// region 增删改相关

	/**
	 * 新增空间用户到空间
	 */
	@PostMapping("/add")
	public BaseResponse<Boolean> addSpaceUserToSpace(@RequestBody SpaceUserAddRequest spaceUserAddRequest) {
		ThrowUtils.throwIf(spaceUserAddRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceUserAddRequest.getSpaceId()), ErrorCode.PARAMS_ERROR, "空间 ID 不能为空");
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceUserAddRequest.getUserId()), ErrorCode.PARAMS_ERROR, "用户 ID 不能为空");
		return ResultUtils.success(spaceUserApplicationService.addSpaceUserToSpace(spaceUserAddRequest));
	}

	/**
	 * 编辑空间用户权限
	 */
	@PostMapping("/edit")
	public BaseResponse<Boolean> editSpaceUserAuth(@RequestBody SpaceUserEditRequest spaceUserEditRequest) {
		ThrowUtils.throwIf(spaceUserEditRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceUserEditRequest.getId()), ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(StrUtil.isEmpty(spaceUserEditRequest.getSpaceRole()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(spaceUserApplicationService.editSpaceUserAuth(spaceUserEditRequest));
	}

	/**
	 * 删除空间用户
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(ObjectUtil.isEmpty(deleteRequest.getId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(spaceUserApplicationService.deleteSpaceUser(deleteRequest));
	}

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 根据空间 ID 查询空间用户分页列表
	 */
	@GetMapping("/list")
	public BaseResponse<PageVO<SpaceUserVO>> getSpaceUserPageListBySpaceId(SpaceUserQueryRequest spaceUserQueryRequest) {
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceUserQueryRequest.getSpaceId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(spaceUserApplicationService.getSpaceUserPageListBySpaceId(spaceUserQueryRequest));
	}

	// endregion 查询相关
}
