package com.baolong.pictures.infrastructure.persistence.category.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.category.aggregate.Category;
import com.baolong.pictures.domain.category.repository.CategoryRepository;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.category.converter.CategoryConverter;
import com.baolong.pictures.infrastructure.persistence.category.mybatis.CategoryDO;
import com.baolong.pictures.infrastructure.persistence.category.mybatis.CategoryPersistenceService;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 分类表 (category) - 仓储服务实现
 *
 * @author Baolong 2025年03月21 21:00
 * @version 1.0
 * @since 1.8
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

	private final CategoryPersistenceService categoryPersistenceService;

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param category 分类领域对象
	 * @return 查询条件对象（Lambda）
	 */
	private LambdaQueryWrapper<CategoryDO> lambdaQueryWrapper(Category category) {
		LambdaQueryWrapper<CategoryDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		Long categoryId = category.getCategoryId();
		String name = category.getName();
		Long parentId = category.getParentId();
		Integer useNum = category.getUseNum();
		Long userId = category.getUserId();
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(categoryId), CategoryDO::getId, categoryId);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(name), CategoryDO::getName, name);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(parentId), CategoryDO::getParentId, parentId);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(useNum), CategoryDO::getUseNum, useNum);
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(userId), CategoryDO::getUserId, userId);
		// 处理排序规则
		if (category.isMultipleSort()) {
			List<PageRequest.Sort> sorts = category.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(
							StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(CategoryDO.class, sortField)
					);
				});
			}
		} else {
			PageRequest.Sort sort = category.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(
						StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(CategoryDO.class, sortField)
				);
			} else {
				lambdaQueryWrapper.orderByDesc(CategoryDO::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	/**
	 * 新增分类
	 *
	 * @param category 分类领域对象
	 * @return 是否成功
	 */
	@Override
	public boolean addCategory(Category category) {
		return categoryPersistenceService.save(CategoryConverter.toDO(category));
	}

	/**
	 * 删除分类
	 *
	 * @param categoryId 分类ID
	 * @return 是否成功
	 */
	@Override
	public boolean deleteCategory(Long categoryId) {
		return categoryPersistenceService.removeById(categoryId);
	}

	/**
	 * 更新分类
	 *
	 * @param category 分类领域对象
	 * @return 是否成功
	 */
	@Override
	public boolean updateCategory(Category category) {
		return categoryPersistenceService.updateById(CategoryConverter.toDO(category));
	}

	/**
	 * 获取分类列表
	 *
	 * @return 分类列表
	 */
	@Override
	public List<Category> getCategoryList() {
		List<CategoryDO> categoryDOList = categoryPersistenceService.list();
		return CategoryConverter.toDomainList(categoryDOList);
	}

	/**
	 * 获取分类分页列表
	 *
	 * @param category 分类领域对象
	 * @return 分类分页列表
	 */
	@Override
	public PageVO<Category> getCategoryPageList(Category category) {
		LambdaQueryWrapper<CategoryDO> lambdaQueryWrapper = this.lambdaQueryWrapper(category);
		Page<CategoryDO> page = categoryPersistenceService.page(category.getPage(CategoryDO.class), lambdaQueryWrapper);
		return CategoryConverter.toDomainPage(page);
	}

	/**
	 * 根据分类ID列表获取分类列表
	 *
	 * @param categoryIds 分类ID列表
	 * @return 分类列表
	 */
	@Override
	public List<Category> getCategoryListByCategoryIds(Set<Long> categoryIds) {
		List<CategoryDO> categoryDOList = categoryPersistenceService.listByIds(categoryIds);
		return CategoryConverter.toDomainList(categoryDOList);
	}

	/**
	 * 根据分类ID获取分类
	 *
	 * @param categoryId 分类ID
	 * @return 分类
	 */
	@Override
	public Category getCategoryByCategoryId(Long categoryId) {
		CategoryDO categoryDO = categoryPersistenceService.getById(categoryId);
		if (categoryDO == null) return null;
		return CategoryConverter.toDomain(categoryDO);
	}
}
