package com.baolong.pictures.domain.picture.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片分享状态枚举
 */
@Getter
public enum PictureShareStatusEnum {
	SHARE(0, "允许分享"),
	NO_SHARE(1, "不允许分享");

	private final Integer key;

	private final String label;

	PictureShareStatusEnum(int key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static PictureShareStatusEnum of(Integer key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static PictureShareStatusEnum getEnumByKey(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (PictureShareStatusEnum anEnum : PictureShareStatusEnum.values()) {
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
				.map(PictureShareStatusEnum::getKey)
				.collect(Collectors.toList());
	}

	/**
	 * 判断是否为分享状态
	 *
	 * @param key 状态键值
	 * @return 是否分享
	 */
	public static boolean isShare(Integer key) {
		return SHARE.getKey().equals(key);
	}
}
