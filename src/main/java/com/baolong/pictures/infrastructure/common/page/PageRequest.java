package com.baolong.pictures.infrastructure.common.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页请求
 *
 * @author Baolong 2025年03月07 13:32
 * @version 1.0
 * @since 1.8
 */
@Data
public class PageRequest implements Serializable {

	/**
	 * 当前页
	 */
	private int current = 1;

	/**
	 * 每页显示条数, 默认 10
	 */
	private int pageSize = 10;

	/**
	 * 是否多个排序, true 为多个排序, false 为单个排序
	 */
	private boolean multipleSort = false;

	/**
	 * 排序参数
	 */
	private Sort sort;

	/**
	 * 排序参数列表
	 */
	private List<Sort> sorts;

	/**
	 * 排序类
	 */
	@Data
	public static class Sort {
		/**
		 * 是否升序, true 为升序, false 为降序
		 */
		private boolean asc = false;
		/**
		 * 排序字段
		 */
		private String field;
	}

	/**
	 * 获取分页对象
	 *
	 * @param clazz 类
	 * @param <T>   泛型
	 * @return 分页对象, MyBatisPlus 的分页
	 */
	public <T> Page<T> getPage(Class<T> clazz) {
		return new Page<T>(this.current, this.pageSize);
	}

	private static final long serialVersionUID = 1L;
}
