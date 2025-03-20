package com.baolong.pictures.infrastructure.repository;

import com.baolong.pictures.domain.category.entity.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 分类表 (category) - 仓储服务接口, 操作数据库 Mapper
*
* @author Baolong 2025-03-08 01:39:38
*/
public interface CategoryRepository extends BaseMapper<Category> {
}
