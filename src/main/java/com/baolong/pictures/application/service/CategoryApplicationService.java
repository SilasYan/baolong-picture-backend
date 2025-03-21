package com.baolong.pictures.application.service;

import com.baolong.pictures.domain.category.aggregate.Category;
import com.baolong.pictures.domain.category.service.CategoryDomainService;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 分类表 (category) - 应用服务
 *
 * @author Baolong 2025年03月09 21:08
 * @version 1.0
 * @since 1.8
 */
@Service
@RequiredArgsConstructor
public class CategoryApplicationService {

	private final CategoryDomainService categoryDomainService;

	/**
	 * 新增分类
	 *
	 * @param category 分类领域对象
	 */
	public void addCategory(Category category) {
		categoryDomainService.addCategory(category);
	}

	/**
	 * 删除分类
	 *
	 * @param categoryId 分类ID
	 */
	public void deleteCategory(Long categoryId) {
		categoryDomainService.deleteCategory(categoryId);
	}

	/**
	 * 更新分类
	 *
	 * @param category 分类领域对象
	 */
	public void updateCategory(Category category) {
		categoryDomainService.updateCategory(category);
	}

	/**
	 * 获取首页分类列表
	 *
	 * @return 首页分类列表
	 */
	public List<Category> getCategoryListAsHome() {
		return categoryDomainService.getCategoryListAsHome();
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param category 分类领域对象
	 * @return 图片管理分页列表
	 */
	public PageVO<Category> getCategoryPageListAsManage(Category category) {
		return categoryDomainService.getCategoryPageListAsManage(category);
	}

	/**
	 * 根据分类ID列表获取分类列表
	 *
	 * @param categoryIds 分类ID列表
	 * @return 分类列表
	 */
	public List<Category> getCategoryListByCategoryIds(Set<Long> categoryIds) {
		return categoryDomainService.getCategoryListByCategoryIds(categoryIds);
	}

	/**
	 * 根据分类ID获取分类
	 *
	 * @param categoryId 分类ID
	 * @return 分类
	 */
	public Category getCategoryByCategoryId(Long categoryId) {
		return categoryDomainService.getCategoryByCategoryId(categoryId);
	}
}
