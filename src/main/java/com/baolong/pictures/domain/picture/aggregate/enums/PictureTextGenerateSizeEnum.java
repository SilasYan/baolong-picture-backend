package com.baolong.pictures.domain.picture.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片文本生成尺寸枚举
 */
@Getter
public enum PictureTextGenerateSizeEnum {
	SIZE_1_1(0, "1024*1024"),
	SIZE_16_9(1, "1024*768"),
	;

	private final Integer key;

	private final String label;

	PictureTextGenerateSizeEnum(int key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static PictureTextGenerateSizeEnum of(Integer key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static PictureTextGenerateSizeEnum getEnumByKey(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (PictureTextGenerateSizeEnum anEnum : PictureTextGenerateSizeEnum.values()) {
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
	public static List<Integer> keys() {
		return Arrays.stream(values())
				.map(PictureTextGenerateSizeEnum::getKey)
				.collect(Collectors.toList());
	}
}
