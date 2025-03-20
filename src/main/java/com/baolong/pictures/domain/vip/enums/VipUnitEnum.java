package com.baolong.pictures.domain.vip.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 会员单位枚举
 *
 * @author Baolong 2025年03月08 01:08
 * @version 1.0
 * @since 1.8
 */
@Getter
public enum VipUnitEnum {

	DAY("DAY", "天"),
	MONTH("MONTH", "月"),
	YEAR("YEAR", "年"),
	PERM("PERM", "永久");

	private final String key;

	private final String label;

	VipUnitEnum(String key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 key 获取枚举
	 *
	 * @param key 枚举 key
	 * @return key
	 */
	public static VipUnitEnum getEnumByKey(String key) {
		if (ObjUtil.isEmpty(key)) return null;
		for (VipUnitEnum anEnum : VipUnitEnum.values()) {
			if (anEnum.key.equals(key)) {
				return anEnum;
			}
		}
		return null;
	}
}
