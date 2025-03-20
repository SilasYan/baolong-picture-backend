package com.baolong.pictures.application.service;

import com.baolong.pictures.domain.category.entity.Category;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.dto.category.CategoryAddRequest;
import com.baolong.pictures.interfaces.dto.category.CategoryQueryRequest;
import com.baolong.pictures.interfaces.dto.category.CategoryUpdateRequest;
import com.baolong.pictures.interfaces.vo.category.CategoryVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;
import java.util.Set;

/**
 * 分类应用服务接口
 *
 * @author Baolong 2025年03月09 21:07
 * @version 1.0
 * @since 1.8
 */
public interface CategoryApplicationService {

	// region 增删改

	/**
	 * 新增分类
	 *
	 * @param categoryAddRequest 分类新增请求
	 * @return 是否成功
	 */
	Boolean addCategory(CategoryAddRequest categoryAddRequest);

	/**
	 * 删除分类
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	Boolean deleteCategory(DeleteRequest deleteRequest);

	/**
	 * 更新分类
	 *
	 * @param categoryUpdateRequest 分类更新请求
	 * @return 是否成功
	 */
	Boolean updateCategory(CategoryUpdateRequest categoryUpdateRequest);

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
	 * 获取首页分类列表
	 *
	 * @return 首页分类列表
	 */
	List<CategoryVO> getCategoryListAsHome();

	/**
	 * 获取图片管理分页列表
	 *
	 * @param categoryQueryRequest 分类查询请求
	 * @return 图片管理分页列表
	 */
	PageVO<Category> getCategoryPageListAsManage(CategoryQueryRequest categoryQueryRequest);

	/**
	 * 根据分类 ID 获取分类信息
	 *
	 * @param categoryId 分类 ID
	 * @return 分类信息
	 */
	Category getCategoryInfoById(Long categoryId);

	// endregion 查询相关

	// region 其他方法

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param categoryQueryRequest 分类查询请求
	 * @return 查询条件对象（Lambda）
	 */
	LambdaQueryWrapper<Category> getLambdaQueryWrapper(CategoryQueryRequest categoryQueryRequest);

	// endregion 其他方法
}
