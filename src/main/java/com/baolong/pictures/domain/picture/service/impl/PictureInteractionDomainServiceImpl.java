package com.baolong.pictures.domain.picture.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baolong.pictures.domain.picture.entity.PictureInteraction;
import com.baolong.pictures.domain.picture.service.PictureInteractionDomainService;
import com.baolong.pictures.infrastructure.repository.PictureInteractionRepository;
import org.springframework.stereotype.Service;

/**
* 图片交互表 (picture_interaction) - 领域服务实现
*
* @author Baolong 2025-03-18 21:15:23
*/
@Service
public class PictureInteractionDomainServiceImpl extends ServiceImpl<PictureInteractionRepository, PictureInteraction> implements PictureInteractionDomainService {
}
