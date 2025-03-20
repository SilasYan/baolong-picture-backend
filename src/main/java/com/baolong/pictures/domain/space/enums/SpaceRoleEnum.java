package com.baolong.pictures.domain.space.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 空间角色枚举
 */
@Getter
public enum SpaceRoleEnum {

	CREATOR("CREATOR", "创建者"),
	VIEWER("VIEWER", "浏览者"),
	EDITOR("EDITOR", "编辑者");

	private final String key;

	private final String label;

	SpaceRoleEnum(String key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 key 获取枚举
	 *
	 * @param key 枚举 key
	 * @return key
	 */
	public static SpaceRoleEnum getEnumByValue(String key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (SpaceRoleEnum anEnum : SpaceRoleEnum.values()) {
			if (anEnum.key.equals(key)) {
				return anEnum;
			}
		}
		return null;
	}

	/**
	 * 获取所有枚举的文本列表
	 *
	 * @return 文本列表
	 */
	public static List<String> getAllTexts() {
		return Arrays.stream(SpaceRoleEnum.values())
				.map(SpaceRoleEnum::getLabel)
				.collect(Collectors.toList());
	}

	/**
	 * 获取所有枚举的值列表
	 *
	 * @return 值列表
	 */
	public static List<String> getAllValues() {
		return Arrays.stream(SpaceRoleEnum.values())
				.map(SpaceRoleEnum::getKey)
				.collect(Collectors.toList());
	}
}
