package com.baolong.pictures.interfaces.assembler;

import com.baolong.pictures.domain.category.entity.Category;
import com.baolong.pictures.interfaces.dto.category.CategoryAddRequest;
import com.baolong.pictures.interfaces.dto.category.CategoryUpdateRequest;
import com.baolong.pictures.interfaces.vo.category.CategoryVO;
import org.springframework.beans.BeanUtils;

/**
 * 分类转换类
 *
 * @author Baolong 2025年03月09 21:12
 * @version 1.0
 * @since 1.8
 */
public class CategoryAssembler {

	/**
	 * 标签新增请求 转为 标签实体
	 */
	public static Category toCategoryEntity(CategoryAddRequest categoryAddRequest) {
		Category category = new Category();
		if (categoryAddRequest != null) {
			BeanUtils.copyProperties(categoryAddRequest, category);
		}
		return category;
	}

	/**
	 * 标签更新请求 转为 标签实体
	 */
	public static Category toCategoryEntity(CategoryUpdateRequest categoryUpdateRequest) {
		Category category = new Category();
		if (categoryUpdateRequest != null) {
			BeanUtils.copyProperties(categoryUpdateRequest, category);
		}
		return category;
	}

	/**
	 * 标签实体 转为 标签 VO
	 */
	public static CategoryVO toCategoryVO(Category category) {
		CategoryVO categoryVO = new CategoryVO();
		if (category != null) {
			BeanUtils.copyProperties(category, categoryVO);
		}
		return categoryVO;
	}
}
