package com.baolong.pictures.domain.operate.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baolong.pictures.domain.operate.entity.Operate;
import com.baolong.pictures.domain.operate.service.OperateDomainService;
import com.baolong.pictures.infrastructure.repository.OperateRepository;
import org.springframework.stereotype.Service;

/**
* 操作表 (operate) - 领域服务实现
*
* @author Baolong 2025-03-19 23:07:32
*/
@Service
public class OperateDomainServiceImpl extends ServiceImpl<OperateRepository, Operate> implements OperateDomainService {
}
