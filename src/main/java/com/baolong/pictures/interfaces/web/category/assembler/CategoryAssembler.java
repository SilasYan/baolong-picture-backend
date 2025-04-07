package com.baolong.pictures.interfaces.web.category.assembler;

import com.baolong.pictures.domain.category.aggregate.Category;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.category.request.CategoryAddRequest;
import com.baolong.pictures.interfaces.web.category.request.CategoryQueryRequest;
import com.baolong.pictures.interfaces.web.category.request.CategoryUpdateRequest;
import com.baolong.pictures.interfaces.web.category.response.CategoryVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 分类转换类
 *
 * @author Baolong 2025年03月09 21:12
 * @version 1.0
 * @since 1.8
 */
public class CategoryAssembler {

	/**
	 * 分类新增请求 转为 分类领域对象
	 */
	public static Category toDomain(CategoryAddRequest categoryAddRequest) {
		Category category = new Category();
		BeanUtils.copyProperties(categoryAddRequest, category);
		return category;
	}

	/**
	 * 分类更新请求 转为 分类领域对象
	 */
	public static Category toDomain(CategoryUpdateRequest categoryUpdateRequest) {
		Category category = new Category();
		BeanUtils.copyProperties(categoryUpdateRequest, category);
		return category;
	}

	/**
	 * 分类更新请求 转为 分类领域对象
	 */
	public static Category toDomain(CategoryQueryRequest categoryQueryRequest) {
		Category category = new Category();
		BeanUtils.copyProperties(categoryQueryRequest, category);
		return category;
	}

	/**
	 * 分类领域对象 转为 分类响应对象
	 */
	public static CategoryVO toCategoryVO(Category category) {
		CategoryVO categoryVO = new CategoryVO();
		BeanUtils.copyProperties(category, categoryVO);
		return categoryVO;
	}

	/**
	 * 分类领域对象列表 转为 分类响应对象列表
	 */
	public static List<CategoryVO> toCategoryVOList(List<Category> categoryList) {
		return Optional.ofNullable(categoryList)
				.orElse(List.of())
				.stream()
				.map(CategoryAssembler::toCategoryVO)
				.collect(Collectors.toList());
	}

	/**
	 * 分类领域对象分页 转为 分类响应对象分页
	 */
	public static PageVO<CategoryVO> toCategoryVOPageVO(PageVO<Category> categoryPageVO) {
		return new PageVO<>(categoryPageVO.getCurrent()
				, categoryPageVO.getPageSize()
				, categoryPageVO.getTotal()
				, categoryPageVO.getPages()
				, Optional.ofNullable(categoryPageVO.getRecords())
				.orElse(List.of())
				.stream()
				.map(CategoryAssembler::toCategoryVO)
				.collect(Collectors.toList())
		);
	}
}
