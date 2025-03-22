package com.baolong.pictures.interfaces.web.picture.assembler;

import com.baolong.pictures.domain.picture.aggregate.PictureInteraction;
import com.baolong.pictures.interfaces.web.picture.request.PictureInteractionRequest;
import org.springframework.beans.BeanUtils;

/**
 * 图片互动转换类
 *
 * @author Baolong 2025年03月05 20:50
 * @version 1.0
 * @since 1.8
 */
public class PictureInteractionAssembler {

	/**
	 * 图片互动请求 转为 图片互动领域对象
	 *
	 * @param pictureInteractionRequest 图片互动请求
	 * @return 图片互动领域对象
	 */
	public static PictureInteraction toDomain(PictureInteractionRequest pictureInteractionRequest) {
		PictureInteraction pictureInteraction = new PictureInteraction();
		BeanUtils.copyProperties(pictureInteractionRequest, pictureInteraction);
		return pictureInteraction;
	}
}
