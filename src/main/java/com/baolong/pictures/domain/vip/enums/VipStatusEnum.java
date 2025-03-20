package com.baolong.pictures.domain.vip.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 会员状态枚举
 *
 * @author Baolong 2025年03月08 01:08
 * @version 1.0
 * @since 1.8
 */
@Getter
public enum VipStatusEnum {

	UNUSED(0, "未使用"),
	USED(1, "已使用"),
	EXPIRE(2, "已过期");

	private final Integer key;

	private final String label;

	VipStatusEnum(Integer key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 key 获取枚举
	 *
	 * @param key 枚举 key
	 * @return key
	 */
	public static VipStatusEnum getEnumByKey(Integer key) {
		if (ObjUtil.isEmpty(key)) return null;
		for (VipStatusEnum anEnum : VipStatusEnum.values()) {
			if (anEnum.key.equals(key)) {
				return anEnum;
			}
		}
		return null;
	}
}
