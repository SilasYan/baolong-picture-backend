package com.baolong.pictures.infrastructure.api.grab.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 爬取来源枚举
 */
@Getter
public enum GrabSourceEnum {

	BING("BING", "必应", ""),
	BAIDU("BAIDU", "百度","");

	private final String key;
	private final String label;
	private final String url;

	GrabSourceEnum(String key, String label,String url) {
		this.key = key;
		this.label = label;
		this.url = url;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static GrabSourceEnum of(String key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static GrabSourceEnum getEnumByKey(String key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (GrabSourceEnum anEnum : GrabSourceEnum.values()) {
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
				.map(GrabSourceEnum::getKey)
				.collect(Collectors.toList());
	}
}
