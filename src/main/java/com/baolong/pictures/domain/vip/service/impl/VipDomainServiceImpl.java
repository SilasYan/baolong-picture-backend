package com.baolong.pictures.domain.vip.service.impl;

import com.baolong.pictures.domain.vip.entity.Vip;
import com.baolong.pictures.domain.vip.service.VipDomainService;
import com.baolong.pictures.infrastructure.repository.VipRepository;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 会员表 (会员表) - 领域服务实现
 *
 * @author Baolong 2025年03月08 01:27
 * @version 1.0
 * @since 1.8
 */
@Service
public class VipDomainServiceImpl extends ServiceImpl<VipRepository, Vip> implements VipDomainService {
}
