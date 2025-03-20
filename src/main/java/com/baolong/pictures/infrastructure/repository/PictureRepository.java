package com.baolong.pictures.infrastructure.repository;

import com.baolong.pictures.domain.picture.entity.Picture;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 图片表 (picture) - 仓储服务接口, 操作数据库 Mapper
*
* @author Baolong 2025-03-08 01:39:41
*/
public interface PictureRepository extends BaseMapper<Picture> {
}
