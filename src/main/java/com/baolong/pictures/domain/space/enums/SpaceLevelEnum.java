package com.baolong.pictures.domain.space.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 空间级别枚举
 */
@Getter
public enum SpaceLevelEnum {

	// 1M = 1024KB, 这里用的 B
	COMMON(0, "普通版", 100, 100L * 1024 * 1024),
	PROFESSIONAL(1, "专业版", 1000, 1000L * 1024 * 1024),
	FLAGSHIP(2, "💎旗舰版", 10000, 10000L * 1024 * 1024);

	private final int key;

	private final String label;

	private final long maxCount;

	private final long maxSize;

	SpaceLevelEnum(int key, String label, long maxCount, long maxSize) {
		this.key = key;
		this.label = label;
		this.maxCount = maxCount;
		this.maxSize = maxSize;
	}

	/**
	 * 根据 key 获取枚举
	 *
	 * @param key 枚举 key
	 * @return key
	 */
	public static SpaceLevelEnum getEnumByValue(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (SpaceLevelEnum spaceLevelEnum : SpaceLevelEnum.values()) {
			if (spaceLevelEnum.key == key) {
				return spaceLevelEnum;
			}
		}
		return null;
	}
}
