package com.baolong.pictures.domain.space.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
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
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static SpaceRoleEnum of(String key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static SpaceRoleEnum getEnumByKey(String key) {
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
	 * 获取所有有效的 KEY 列表
	 *
	 * @return 有效 KEY 集合（不可变列表）
	 */
	public static List<String> keys() {
		return Arrays.stream(values())
				.map(SpaceRoleEnum::getKey)
				.collect(Collectors.toList());
	}

	/**
	 * 判断是否为创建者
	 *
	 * @param key 状态键值
	 * @return 是否存在
	 */
	public static boolean isCreator(String key) {
		return CREATOR.getKey().equals(key);
	}

	/**
	 * 判断是否为编辑者
	 *
	 * @param key 状态键值
	 * @return 是否存在
	 */
	public static boolean isEditor(String key) {
		return EDITOR.getKey().equals(key);
	}
}
