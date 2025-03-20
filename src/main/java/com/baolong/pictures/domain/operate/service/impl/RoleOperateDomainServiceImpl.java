package com.baolong.pictures.domain.operate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baolong.pictures.domain.operate.entity.RoleOperate;
import com.baolong.pictures.domain.operate.service.RoleOperateDomainService;
import com.baolong.pictures.infrastructure.repository.RoleOperateRepository;
import org.springframework.stereotype.Service;

/**
* 角色操作关联表 (role_operate) - 领域服务实现
*
* @author Baolong 2025-03-19 23:13:03
*/
@Service
public class RoleOperateDomainServiceImpl extends ServiceImpl<RoleOperateRepository, RoleOperate> implements RoleOperateDomainService {
}
