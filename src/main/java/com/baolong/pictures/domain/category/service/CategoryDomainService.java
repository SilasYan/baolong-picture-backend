package com.baolong.pictures.domain.category.service;

import com.baolong.pictures.domain.category.entity.Category;
import com.baolong.pictures.interfaces.dto.category.CategoryQueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

/**
 * 分类表 (category) - 领域服务接口
 *
 * @author Baolong 2025年03月09 21:09
 * @version 1.0
 * @since 1.8
 */
public interface CategoryDomainService extends IService<Category>  {

	// region 其他方法

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param categoryQueryRequest 分类查询请求
	 * @return 查询条件对象（Lambda）
	 */
	LambdaQueryWrapper<Category> getLambdaQueryWrapper(CategoryQueryRequest categoryQueryRequest);

	// endregion 其他方法

	// region 增删改

	/**
	 * 新增分类
	 *
	 * @param category 分类对象
	 * @return 是否成功
	 */
	Boolean addCategory(Category category);

	/**
	 * 删除分类
	 *
	 * @param categoryId 分类 ID
	 * @return 是否成功
	 */
	Boolean deleteCategory(Long categoryId);

	/**
	 * 更新分类
	 *
	 * @param category 分类对象
	 * @return 是否成功
	 */
	Boolean updateCategory(Category category);

	// endregion 增删改

	// region 查询相关

	/**
	 * 获取分类列表
	 *
	 * @return 分类列表
	 */
	List<Category> getCategoryList();

	/**
	 * 根据分类 ID 列表获取分类列表
	 *
	 * @param categoryIds 分类 ID 列表
	 * @return 分类列表
	 */
	List<Category> getCategoryListByIds(Set<Long> categoryIds);

	/**
	 * 获取图片管理分页列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 图片管理分页列表
	 */
	Page<Category> getCategoryPageListAsManage(Page<Category> page, LambdaQueryWrapper<Category> lambdaQueryWrapper);

	/**
	 * 根据分类 ID 获取分类信息
	 *
	 * @param categoryId 分类 ID
	 * @return 分类信息
	 */
	Category getCategoryInfoById(Long categoryId);

	// endregion 查询相关
}
