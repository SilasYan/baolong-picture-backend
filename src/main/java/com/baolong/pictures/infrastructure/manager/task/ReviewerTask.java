package com.baolong.pictures.infrastructure.manager.task;

import cn.hutool.core.collection.CollUtil;
import com.baolong.pictures.domain.picture.aggregate.enums.PictureReviewStatusEnum;
import com.baolong.pictures.infrastructure.manager.message.EmailManager;
import com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis.PictureDO;
import com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis.PicturePersistenceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 审核定时任务类
 *
 * @author Baolong 2025年03月19 13:55
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Component
public class ReviewerTask {

	@Resource
	private PicturePersistenceService picturePersistenceService;
	@Resource
	private EmailManager emailManager;

	/**
	 * 获取待审核图片并发送邮件消息
	 *
	 * @return Task实例 - 使用匿名内部类实现
	 */
	@Bean
	public Task sendReviewerMessageTask() {
		return () -> {
			log.info("开始执行定时任务：发送未审核图片邮件");
			List<PictureDO> noReviewerPictures = picturePersistenceService.list(new LambdaQueryWrapper<PictureDO>()
					.eq(PictureDO::getReviewStatus, PictureReviewStatusEnum.REVIEWING)
			);
			if (CollUtil.isNotEmpty(noReviewerPictures)) {
				log.info("有新的图片待审核，数量为：{}", noReviewerPictures.size());
				emailManager.sendEmailAsText("有新的图片待审核", "待审核图片数量为：" + noReviewerPictures.size());
			}
			log.info("定时任务执行完毕");
		};
	}
}
