package com.baolong.pictures.infrastructure.persistence.picture.picture.converter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.map.MapUtil;
import com.baolong.pictures.domain.picture.aggregate.Picture;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.picture.picture.mybatis.PictureDO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 模型转化类（领域模型 <=> 持久化模型）
 *
 * @author Baolong 2025年03月20 20:42
 * @version 1.0
 * @since 1.8
 */
public class PictureConverter {

	private final static CopyOptions toDoOption = CopyOptions.create();
	private final static CopyOptions toDomainOption = CopyOptions.create();

	static {
		toDoOption.setFieldMapping(MapUtil.of("pictureId", "id"));
		toDomainOption.setFieldMapping(MapUtil.of("id", "pictureId"));
	}

	/**
	 * 领域模型 转为 持久化模型
	 */
	public static PictureDO toDO(Picture picture) {
		PictureDO pictureDO = new PictureDO();
		BeanUtil.copyProperties(picture, pictureDO, toDoOption);
		return pictureDO;
	}

	/**
	 * 领域模型列表 转为 持久化模型列表
	 */
	public static List<PictureDO> toDOList(List<Picture> pictureList) {
		return Optional.ofNullable(pictureList)
				.orElse(List.of()).stream()
				.map(PictureConverter::toDO)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型 转为 领域模型
	 */
	public static Picture toDomain(PictureDO pictureDO) {
		Picture picture = new Picture();
		BeanUtil.copyProperties(pictureDO, picture, toDomainOption);
		return picture;
	}

	/**
	 * 持久化模型列表 转为 领域模型列表
	 */
	public static List<Picture> toDomainList(List<PictureDO> pictureDOList) {
		return Optional.ofNullable(pictureDOList)
				.orElse(List.of())
				.stream().map(PictureConverter::toDomain)
				.collect(Collectors.toList());
	}

	/**
	 * 持久化模型分页 转为 领域模型分页
	 */
	public static PageVO<Picture> toDomainPage(Page<PictureDO> pictureDOPage) {
		return new PageVO<>(
				pictureDOPage.getCurrent()
				, pictureDOPage.getSize()
				, pictureDOPage.getTotal()
				, pictureDOPage.getPages()
				, Optional.ofNullable(pictureDOPage.getRecords())
				.orElse(List.of())
				.stream().map(PictureConverter::toDomain)
				.collect(Collectors.toList())
		);
	}
}
