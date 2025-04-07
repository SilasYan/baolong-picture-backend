package com.baolong.pictures.domain.category.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.domain.category.aggregate.Category;
import com.baolong.pictures.domain.category.repository.CategoryRepository;
import com.baolong.pictures.infrastructure.common.constant.CacheKeyConstant;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.config.LocalCacheConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 分类表 (category) - 领域服务
 *
 * @author Baolong 2025年03月09 21:10
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryDomainService {

	private final CategoryRepository categoryRepository;

	/**
	 * 新增分类
	 *
	 * @param category 分类领域对象
	 */
	public void addCategory(Category category) {
		category.setUserId(StpUtil.getLoginIdAsLong());
		boolean result = categoryRepository.addCategory(category);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "新增分类失败");
	}

	/**
	 * 删除分类
	 *
	 * @param categoryId 分类 ID
	 */
	public void deleteCategory(Long categoryId) {
		boolean result = categoryRepository.deleteCategory(categoryId);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "删除分类失败");
	}

	/**
	 * 更新分类
	 *
	 * @param category 分类领域对象
	 */
	public void updateCategory(Category category) {
		boolean result = categoryRepository.updateCategory(category);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "更新分类失败");
	}

	/**
	 * 获取首页分类列表
	 *
	 * @return 首页分类列表
	 */
	public List<Category> getCategoryListAsHome() {
		List<Category> categoryList;
		String localData = LocalCacheConfig.HOME_PICTURE_LOCAL_CACHE.getIfPresent(CacheKeyConstant.HOME_CATEGORY);
		if (StrUtil.isNotEmpty(localData)) {
			log.info("首页分类列表[Local 缓存]");
			categoryList = JSONUtil.toBean(localData, new TypeReference<List<Category>>() {
			}, true);
		} else {
			log.info("首页分类列表[MySQL 查询]");
			categoryList = categoryRepository.getCategoryList();
			LocalCacheConfig.HOME_PICTURE_LOCAL_CACHE.put(CacheKeyConstant.HOME_CATEGORY, JSONUtil.toJsonStr(categoryList));
		}
		return categoryList;
	}

	/**
	 * 根据分类ID列表获取分类列表
	 *
	 * @param categoryIds 分类ID列表
	 * @return 分类列表
	 */
	public List<Category> getCategoryListByCategoryIds(Set<Long> categoryIds) {
		return categoryRepository.getCategoryListByCategoryIds(categoryIds);
	}

	/**
	 * 获取图片管理分页列表
	 *
	 * @param category 分类领域对象
	 * @return 图片管理分页列表
	 */
	public PageVO<Category> getCategoryPageListAsManage(Category category) {
		return categoryRepository.getCategoryPageList(category);
	}

	/**
	 * 根据分类ID获取分类
	 *
	 * @param categoryId 分类ID
	 * @return 分类
	 */
	public Category getCategoryByCategoryId(Long categoryId) {
		return categoryRepository.getCategoryByCategoryId(categoryId);
	}
}
