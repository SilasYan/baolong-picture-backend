package com.baolong.pictures.infrastructure.persistence.category.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.baolong.pictures.domain.category.aggregate.Category;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.category.mybatis.CategoryDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模型转化类（领域模型 <=> 持久化模型）
 *
 * @author Baolong 2025年03月20 20:42
 * @version 1.0
 * @since 1.8
 */
public class CategoryConverter {

	private final static CopyOptions toDoOption = CopyOptions.create();
	private final static CopyOptions toDomainOption = CopyOptions.create();

	static {
		toDoOption.setFieldMapping(MapUtil.of("categoryId", "id"));
		toDomainOption.setFieldMapping(MapUtil.of("id", "categoryId"));
	}

	/**
	 * 领域模型 转为 持久化模型
	 */
	public static CategoryDO toDO(Category category) {
		CategoryDO categoryDO = new CategoryDO();
		BeanUtil.copyProperties(category, categoryDO, toDoOption);
		return categoryDO;
	}

	/**
	 * 领域模型列表 转为 持久化模型列表
	 */
	public static List<CategoryDO> toDOList(List<Category> categoryList) {
		return Optional.ofNullable(categoryList)
				.orElse(List.of()).stream()
				.map(CategoryConverter::toDO)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型 转为 领域模型
	 */
	public static Category toDomain(CategoryDO categoryDO) {
		Category category = new Category();
		BeanUtil.copyProperties(categoryDO, category, toDomainOption);
		return category;
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 */
	public static List<Category> toDomainList(List<CategoryDO> userDOList) {
		return Optional.ofNullable(userDOList)
				.orElse(List.of())
				.stream().map(CategoryConverter::toDomain)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 */
	public static PageVO<Category> toDomainPage(Page<CategoryDO> categoryDOPage) {
		return new PageVO<>(
				categoryDOPage.getCurrent()
				, categoryDOPage.getSize()
				, categoryDOPage.getTotal()
				, categoryDOPage.getPages()
				, Optional.ofNullable(categoryDOPage.getRecords())
				.orElse(List.of()).stream()
				.map(CategoryConverter::toDomain)
				.collect(Collectors.toList())
		);
	}
}
