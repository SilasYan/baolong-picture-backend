package com.baolong.pictures.infrastructure.common.page;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页 VO
 * <p>
 * 适用于 MyBatisPlus 的分页对象
 *
 * @author Baolong 2025年03月07 13:32
 * @version 1.0
 * @since 1.8
 */
@Data
public class PageVO<T> implements Serializable {

	/**
	 * 当前页
	 */
	private long current = 1;

	/**
	 * 每页显示条数，默认 10
	 */
	private long pageSize = 10;

	/**
	 * 总数
	 */
	private long total = 0;

	/**
	 * 总页数
	 */
	private long pages = 0;

	/**
	 * 查询数据列表
	 */
	private List<T> records = Collections.emptyList();

	public PageVO() {
	}

	public PageVO(long current, long pageSize, long total, long pages, List<T> records) {
		this.current = current;
		this.pageSize = pageSize;
		this.total = total;
		this.pages = pages;
		this.records = records;
	}

	/**
	 * 转换
	 *
	 * @param page 分页对象（MyBatisPlus 的分页对象）
	 * @param <T>  泛型
	 * @return 分页对象 VO
	 */
	public static <T> PageVO<T> from(IPage<T> page) {
		PageVO<T> pageVO = new PageVO<>();
		pageVO.setCurrent(page.getCurrent());
		pageVO.setPageSize(page.getSize());
		pageVO.setTotal(page.getTotal());
		pageVO.setPages(page.getPages());
		pageVO.setRecords(page.getRecords());
		return pageVO;
	}

	private static final long serialVersionUID = 1L;
}
