package com.baolong.pictures.domain.menu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baolong.pictures.domain.menu.entity.Menu;
import com.baolong.pictures.domain.menu.service.MenuDomainService;
import com.baolong.pictures.infrastructure.repository.MenuRepository;
import org.springframework.stereotype.Service;

/**
* 菜单表 (menu) - 领域服务实现
*
* @author Baolong 2025-03-19 20:21:10
*/
@Service
public class MenuDomainServiceImpl extends ServiceImpl<MenuRepository, Menu> implements MenuDomainService {
}
