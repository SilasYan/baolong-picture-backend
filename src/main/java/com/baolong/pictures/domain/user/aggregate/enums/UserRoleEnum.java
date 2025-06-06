package com.baolong.pictures.domain.user.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRoleEnum {

	USER("USER", "用户"),
	ADMIN("ADMIN", "管理员");

	private final String key;
	private final String label;

	UserRoleEnum(String key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static UserRoleEnum of(String key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static UserRoleEnum getEnumByKey(String key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (UserRoleEnum anEnum : UserRoleEnum.values()) {
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
				.map(UserRoleEnum::getKey)
				.collect(Collectors.toList());
	}

	/**
	 * 判断是否为管理员
	 *
	 * @param key 状态键值
	 * @return 是否管理员
	 */
	public static boolean isAdmin(String key) {
		return ADMIN.getKey().equals(key);
	}
}
