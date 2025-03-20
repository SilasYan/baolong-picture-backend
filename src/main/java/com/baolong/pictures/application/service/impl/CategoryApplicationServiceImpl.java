package com.baolong.pictures.application.service.impl;

import com.baolong.pictures.application.service.CategoryApplicationService;
import com.baolong.pictures.application.service.UserApplicationService;
import com.baolong.pictures.domain.category.entity.Category;
import com.baolong.pictures.domain.category.service.CategoryDomainService;
import com.baolong.pictures.domain.user.entity.User;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.interfaces.assembler.CategoryAssembler;
import com.baolong.pictures.interfaces.dto.category.CategoryAddRequest;
import com.baolong.pictures.interfaces.dto.category.CategoryQueryRequest;
import com.baolong.pictures.interfaces.dto.category.CategoryUpdateRequest;
import com.baolong.pictures.interfaces.vo.category.CategoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 分类应用服务实现
 *
 * @author Baolong 2025年03月09 21:08
 * @version 1.0
 * @since 1.8
 */
@Service
@RequiredArgsConstructor
public class CategoryApplicationServiceImpl implements CategoryApplicationService {

	private final CategoryDomainService categoryDomainService;
	private final UserApplicationService userApplicationService;

	// region 增删改

	/**
	 * 新增分类
	 *
	 * @param categoryAddRequest 分类新增请求
	 * @return 是否成功
	 */
	@Override
	public Boolean addCategory(CategoryAddRequest categoryAddRequest) {
		Category category = CategoryAssembler.toCategoryEntity(categoryAddRequest);
		User loginUser = userApplicationService.getLoginUser();
		category.setUserId(loginUser.getId());
		return categoryDomainService.addCategory(category);
	}

	/**
	 * 删除分类
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	@Override
	public Boolean deleteCategory(DeleteRequest deleteRequest) {
		return categoryDomainService.deleteCategory(deleteRequest.getId());
	}

	/**
	 * 更新分类
	 *
	 * @param categoryUpdateRequest 分类更新请求
	 * @return 是否成功
	 */
	@Override
	public Boolean updateCategory(CategoryUpdateRequest categoryUpdateRequest) {
		Category category = CategoryAssembler.toCategoryEntity(categoryUpdateRequest);
		return categoryDomainService.updateCategory(category);
	}

	// endregion 增删改

	// region 查询相关

	/**
	 * 获取分类列表
	 *
	 * @return 分类列表
	 */
	@Override
	public List<Category> getCategoryList() {
		return categoryDomainService.getCategoryList();
	}

	/**
	 * 根据分类 ID 列表获取分类列表
	 *
	 * @param categoryIds 分类 ID 列表
	 * @return 分类列表
	 */
	@Override
	public List<Category> getCategoryListByIds(Set<Long> categoryIds) {
		return categoryDomainService.getCategoryListByIds(categoryIds);
	}

	/**
	 * 获取首页分类列表
	 *
	 * @return 分类列表
	 */
	@Override
	public List<CategoryVO> getCategoryListAsHome() {
		List<Category> categories = categoryDomainService.getCategoryList();
		return categories.stream().map(CategoryAssembler::toCategoryVO).collect(Collectors.toList());
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param categoryQueryRequest 分类查询请求
	 * @return 图片管理分页列表
	 */
	@Override
	public PageVO<Category> getCategoryPageListAsManage(CategoryQueryRequest categoryQueryRequest) {
		Page<Category> picturePage = categoryDomainService.getCategoryPageListAsManage(
				categoryQueryRequest.getPage(Category.class), this.getLambdaQueryWrapper(categoryQueryRequest)
		);
		return PageVO.from(picturePage);
	}

	/**
	 * 根据分类 ID 获取分类信息
	 *
	 * @param categoryId 分类 ID
	 * @return 分类信息
	 */
	@Override
	public Category getCategoryInfoById(Long categoryId) {
		return categoryDomainService.getCategoryInfoById(categoryId);
	}

	// endregion 查询相关

	// region 其他方法

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param categoryQueryRequest 分类查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@Override
	public LambdaQueryWrapper<Category> getLambdaQueryWrapper(CategoryQueryRequest categoryQueryRequest) {
		ThrowUtils.throwIf(categoryQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
		return categoryDomainService.getLambdaQueryWrapper(categoryQueryRequest);
	}

	// endregion 其他方法
}
