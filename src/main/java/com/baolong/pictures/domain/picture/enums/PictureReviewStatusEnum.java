package com.baolong.pictures.domain.picture.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 图片审核状态枚举
 */
@Getter
public enum PictureReviewStatusEnum {
	REVIEWING(0, "待审核"),
	PASS(1, "通过"),
	REJECT(2, "拒绝");

	private final int key;

	private final String label;

	PictureReviewStatusEnum(int key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 key 获取枚举
	 *
	 * @param key 枚举 key
	 * @return key
	 */
	public static PictureReviewStatusEnum getEnumByValue(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (PictureReviewStatusEnum pictureReviewStatusEnum : PictureReviewStatusEnum.values()) {
			if (pictureReviewStatusEnum.key == key) {
				return pictureReviewStatusEnum;
			}
		}
		return null;
	}
}
