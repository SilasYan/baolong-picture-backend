package com.baolong.pictures.interfaces.rest;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.application.service.CategoryApplicationService;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.category.aggregate.Category;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.interfaces.web.category.assembler.CategoryAssembler;
import com.baolong.pictures.interfaces.web.category.request.CategoryAddRequest;
import com.baolong.pictures.interfaces.web.category.request.CategoryQueryRequest;
import com.baolong.pictures.interfaces.web.category.request.CategoryUpdateRequest;
import com.baolong.pictures.interfaces.web.category.response.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类表 (category) - 接口
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

	/**
	 * 新增分类
	 *
	 * @param categoryAddRequest 分类新增请求
	 * @return 新增结果
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> addCategory(@RequestBody CategoryAddRequest categoryAddRequest) {
		ThrowUtils.throwIf(categoryAddRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(StrUtil.isEmpty(categoryAddRequest.getName()), ErrorCode.PARAMS_ERROR, "分类名称不能为空");
		categoryApplicationService.addCategory(CategoryAssembler.toDomain(categoryAddRequest));
		return ResultUtils.success();
	}

	/**
	 * 删除分类
	 *
	 * @param deleteRequest 分类删除请求
	 * @return 删除结果
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteCategory(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
		Long categoryId = deleteRequest.getId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(categoryId), ErrorCode.PARAMS_ERROR);
		categoryApplicationService.deleteCategory(categoryId);
		return ResultUtils.success();
	}

	/**
	 * 更新分类
	 *
	 * @param categoryUpdateRequest 分类更新请求
	 * @return 更新结果
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateCategory(@RequestBody CategoryUpdateRequest categoryUpdateRequest) {
		ThrowUtils.throwIf(categoryUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(categoryUpdateRequest.getCategoryId()), ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(StrUtil.isEmpty(categoryUpdateRequest.getName()), ErrorCode.PARAMS_ERROR, "分类名称不能为空");
		categoryApplicationService.updateCategory(CategoryAssembler.toDomain(categoryUpdateRequest));
		return ResultUtils.success();
	}

	/**
	 * 获取首页分类列表
	 *
	 * @return 首页分类列表
	 */
	@GetMapping("/home/list")
	public BaseResponse<List<CategoryVO>> getCategoryListAsHome() {
		List<Category> categoryList = categoryApplicationService.getCategoryListAsHome();
		return ResultUtils.success(CategoryAssembler.toCategoryVOList(categoryList));
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param categoryQueryRequest 分类查询请求
	 * @return 图片管理分页列表
	 */
	@PostMapping("/manage/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<CategoryVO>> getCategoryPageListAsManage(@RequestBody CategoryQueryRequest categoryQueryRequest) {
		Category category = CategoryAssembler.toDomain(categoryQueryRequest);
		PageVO<Category> categoryPageVO = categoryApplicationService.getCategoryPageListAsManage(category);
		return ResultUtils.success(CategoryAssembler.toCategoryVOPageVO(categoryPageVO));
	}
}
