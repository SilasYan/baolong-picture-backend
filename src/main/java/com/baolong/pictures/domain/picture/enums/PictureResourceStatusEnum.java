package com.baolong.pictures.domain.picture.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 图片资源状态枚举
 */
@Getter
public enum PictureResourceStatusEnum {
	// 0-存在 COS, 1-不存在 COS
	EXIST(0, "存在 COS"),
	NOT_EXIST(1, "不存在 COS");

	private final int key;

	private final String label;

	PictureResourceStatusEnum(int key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 key 获取枚举
	 *
	 * @param key 枚举 key
	 * @return key
	 */
	public static PictureResourceStatusEnum getEnumByValue(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (PictureResourceStatusEnum pictureReviewStatusEnum : PictureResourceStatusEnum.values()) {
			if (pictureReviewStatusEnum.key == key) {
				return pictureReviewStatusEnum;
			}
		}
		return null;
	}
}
