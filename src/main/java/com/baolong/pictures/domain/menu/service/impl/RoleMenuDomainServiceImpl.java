package com.baolong.pictures.domain.menu.service.impl;

import com.baolong.pictures.domain.menu.service.RoleMenuDomainService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baolong.pictures.domain.menu.entity.RoleMenu;
import com.baolong.pictures.infrastructure.repository.RoleMenuRepository;
import org.springframework.stereotype.Service;

/**
* 角色菜单关联表 (role_menu) - 领域服务实现
*
* @author Baolong 2025-03-19 20:21:10
*/
@Service
public class RoleMenuDomainServiceImpl extends ServiceImpl<RoleMenuRepository, RoleMenu> implements RoleMenuDomainService {
}
