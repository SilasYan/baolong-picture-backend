package com.baolong.pictures.domain.vip.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 会员类型枚举
 *
 * @author Baolong 2025年03月08 01:08
 * @version 1.0
 * @since 1.8
 */
@Getter
public enum VipTypeEnum {

	VIP("VIP", "普通会员"),
	SVIP("SVIP", "超级会员");

	private final String key;

	private final String label;

	VipTypeEnum(String key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 key 获取枚举
	 *
	 * @param key 枚举 key
	 * @return key
	 */
	public static VipTypeEnum getEnumByKey(String key) {
		if (ObjUtil.isEmpty(key)) return null;
		for (VipTypeEnum anEnum : VipTypeEnum.values()) {
			if (anEnum.key.equals(key)) {
				return anEnum;
			}
		}
		return null;
	}
}
