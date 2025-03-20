package com.baolong.pictures.domain.space.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 空间类型枚举
 */
@Getter
public enum SpaceTypeEnum {

	PRIVATE(0, "私有空间"),
	TEAM(1, "团队空间");

	private final int key;

	private final String label;

	SpaceTypeEnum(int key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 key 获取枚举
	 *
	 * @param key 枚举 key
	 * @return key
	 */
	public static SpaceTypeEnum getEnumByValue(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (SpaceTypeEnum spaceTypeEnum : SpaceTypeEnum.values()) {
			if (spaceTypeEnum.key == key) {
				return spaceTypeEnum;
			}
		}
		return null;
	}
}
