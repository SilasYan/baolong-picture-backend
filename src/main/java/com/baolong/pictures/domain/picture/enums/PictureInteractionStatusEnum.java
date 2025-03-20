package com.baolong.pictures.domain.picture.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片交互枚举
 */
@Getter
public enum PictureInteractionStatusEnum {
	EXISTED(0, "存在"),
	CANCEL(1, "取消");

	private final Integer key;

	private final String label;

	PictureInteractionStatusEnum(int key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static PictureInteractionStatusEnum of(Integer key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static PictureInteractionStatusEnum getEnumByKey(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (PictureInteractionStatusEnum anEnum : PictureInteractionStatusEnum.values()) {
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
				.map(PictureInteractionStatusEnum::getKey)
				.collect(Collectors.toList());
	}

	/**
	 * 判断是否为存在
	 *
	 * @param key 状态键值
	 * @return 是否存在
	 */
	public static boolean isExisted(Integer key) {
		return EXISTED.getKey().equals(key);
	}
}
