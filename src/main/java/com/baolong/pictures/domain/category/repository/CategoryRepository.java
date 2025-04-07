package com.baolong.pictures.domain.category.repository;

import com.baolong.pictures.domain.category.aggregate.Category;
import com.baolong.pictures.infrastructure.common.page.PageVO;

import java.util.List;
import java.util.Set;

/**
 * 分类表 (category) - 仓储服务接口
 *
 * @author Baolong 2025年03月21 21:00
 * @version 1.0
 * @since 1.8
 */
public interface CategoryRepository {

	/**
	 * 新增分类
	 *
	 * @param category 分类领域对象
	 * @return 是否成功
	 */
	boolean addCategory(Category category);

	/**
	 * 删除分类
	 *
	 * @param categoryId 分类ID
	 * @return 是否成功
	 */
	boolean deleteCategory(Long categoryId);

	/**
	 * 更新分类
	 *
	 * @param category 分类领域对象
	 * @return 是否成功
	 */
	boolean updateCategory(Category category);

	/**
	 * 获取分类列表
	 *
	 * @return 分类列表
	 */
	List<Category> getCategoryList();

	/**
	 * 获取分类分页列表
	 *
	 * @param category 分类领域对象
	 * @return 分类分页列表
	 */
	PageVO<Category> getCategoryPageList(Category category);

	/**
	 * 根据分类ID列表获取分类列表
	 *
	 * @param categoryIds 分类ID列表
	 * @return 分类列表
	 */
	List<Category> getCategoryListByCategoryIds(Set<Long> categoryIds);

	/**
	 * 根据分类ID获取分类
	 *
	 * @param categoryId 分类ID
	 * @return 分类
	 */
	Category getCategoryByCategoryId(Long categoryId);
}
