package com.baolong.pictures.infrastructure.repository;

import com.baolong.pictures.domain.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 用户表 (user) - 仓储服务, 操作数据库 Mapper 接口
*
* @author Baolong 2025-03-08 01:39:55
*/
public interface UserRepository extends BaseMapper<User> {
}
