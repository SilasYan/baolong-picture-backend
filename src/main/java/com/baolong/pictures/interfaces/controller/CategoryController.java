package com.baolong.pictures.interfaces.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.application.service.CategoryApplicationService;
import com.baolong.pictures.domain.category.entity.Category;
import com.baolong.pictures.domain.user.constant.UserConstant;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.interfaces.dto.category.CategoryAddRequest;
import com.baolong.pictures.interfaces.dto.category.CategoryQueryRequest;
import com.baolong.pictures.interfaces.dto.category.CategoryUpdateRequest;
import com.baolong.pictures.interfaces.vo.category.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类接口
 *
 * @author Baolong 2025年03月09 21:06
 * @version 1.0
 * @since 1.8
 */
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
	private final CategoryApplicationService categoryApplicationService;

	// region 增删改

	/**
	 * 新增分类
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> addCategory(@RequestBody CategoryAddRequest categoryAddRequest) {
		ThrowUtils.throwIf(categoryAddRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(StrUtil.isEmpty(categoryAddRequest.getName()), ErrorCode.PARAMS_ERROR, "分类名称不能为空");
		return ResultUtils.success(categoryApplicationService.addCategory(categoryAddRequest));
	}

	/**
	 * 删除分类
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteCategory(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(deleteRequest.getId()), ErrorCode.PARAMS_ERROR);
		return ResultUtils.success(categoryApplicationService.deleteCategory(deleteRequest));
	}

	/**
	 * 更新分类
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateCategory(@RequestBody CategoryUpdateRequest categoryUpdateRequest) {
		ThrowUtils.throwIf(categoryUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(categoryUpdateRequest.getId()), ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(StrUtil.isEmpty(categoryUpdateRequest.getName()), ErrorCode.PARAMS_ERROR, "分类名称不能为空");
		return ResultUtils.success(categoryApplicationService.updateCategory(categoryUpdateRequest));
	}

	// endregion 增删改

	// region 查询相关

	/**
	 * 获取首页分类列表
	 */
	@GetMapping("/home/list")
	public BaseResponse<List<CategoryVO>> getCategoryListAsHome() {
		return ResultUtils.success(categoryApplicationService.getCategoryListAsHome());
	}

	/**
	 * 获取图片管理分页列表
	 */
	@PostMapping("/manage/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<Category>> getCategoryPageListAsManage(@RequestBody CategoryQueryRequest categoryQueryRequest) {
		return ResultUtils.success(categoryApplicationService.getCategoryPageListAsManage(categoryQueryRequest));
	}

	// endregion 查询相关
}
