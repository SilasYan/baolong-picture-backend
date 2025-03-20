package com.baolong.pictures.domain.category.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.category.entity.Category;
import com.baolong.pictures.domain.category.service.CategoryDomainService;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.repository.CategoryRepository;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baolong.pictures.interfaces.web.request.category.CategoryQueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 分类表 (category) - 领域服务实现
 *
 * @author Baolong 2025年03月09 21:10
 * @version 1.0
 * @since 1.8
 */
@Service
public class CategoryDomainServiceImpl extends ServiceImpl<CategoryRepository, Category> implements CategoryDomainService {

	// region 其他方法

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param categoryQueryRequest 分类查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@Override
	public LambdaQueryWrapper<Category> getLambdaQueryWrapper(CategoryQueryRequest categoryQueryRequest) {
		LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		Long id = categoryQueryRequest.getId();
		String name = categoryQueryRequest.getName();
		Long parentId = categoryQueryRequest.getParentId();
		Integer useNum = categoryQueryRequest.getUseNum();
		Long userId = categoryQueryRequest.getUserId();
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(id), Category::getId, id);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(name), Category::getName, name);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(parentId), Category::getParentId, parentId);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(useNum), Category::getUseNum, useNum);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(userId), Category::getUserId, userId);
		// 处理排序规则
		if (categoryQueryRequest.isMultipleSort()) {
			List<PageRequest.Sort> sorts = categoryQueryRequest.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(
							StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(Category.class, sortField)
					);
				});
			}
		} else {
			PageRequest.Sort sort = categoryQueryRequest.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(
						StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(Category.class, sortField)
				);
			} else {
				lambdaQueryWrapper.orderByDesc(Category::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	// endregion 其他方法

	// region 增删改

	/**
	 * 新增分类
	 *
	 * @param category 分类对象
	 * @return 是否成功
	 */
	@Override
	public Boolean addCategory(Category category) {
		return this.save(category);
	}

	/**
	 * 删除分类
	 *
	 * @param categoryId 分类 ID
	 * @return 是否成功
	 */
	@Override
	public Boolean deleteCategory(Long categoryId) {
		return this.removeById(categoryId);
	}

	/**
	 * 更新分类
	 *
	 * @param category 分类对象
	 * @return 是否成功
	 */
	@Override
	public Boolean updateCategory(Category category) {
		category.fillEditTime();
		return this.updateById(category);
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
		return this.list();
	}

	/**
	 * 根据分类 ID 列表获取分类列表
	 *
	 * @param categoryIds 分类 ID 列表
	 * @return 分类列表
	 */
	@Override
	public List<Category> getCategoryListByIds(Set<Long> categoryIds) {
		return this.listByIds(categoryIds);
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 图片管理分页列表
	 */
	@Override
	public Page<Category> getCategoryPageListAsManage(Page<Category> page, LambdaQueryWrapper<Category> lambdaQueryWrapper) {
		return this.page(page, lambdaQueryWrapper);
	}

	/**
	 * 根据分类 ID 获取分类信息
	 *
	 * @param categoryId 分类 ID
	 * @return 分类信息
	 */
	@Override
	public Category getCategoryInfoById(Long categoryId) {
		return this.getById(categoryId);
	}

	// endregion 查询相关
}
