package com.baolong.pictures.infrastructure.persistence.user.mybatis;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 用户表 (user) - 持久化服务
 *
 * @author Baolong 2025年03月20 20:46
 * @version 1.0
 * @since 1.8
 */
@Service
public class UserPersistenceService extends ServiceImpl<UserMapper, UserDO> {
}
