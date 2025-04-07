package com.baolong.pictures.domain.picture.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 图片上传类型枚举
 */
@Getter
public enum PictureUploadTypeEnum {
	PUBLIC("public", "公共图库"),
	SPACE("space", "通过"),
	TEAM("team", "拒绝");

	private final String key;

	private final String label;

	PictureUploadTypeEnum(String key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static PictureUploadTypeEnum of(String key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static PictureUploadTypeEnum getEnumByKey(String key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (PictureUploadTypeEnum anEnum : PictureUploadTypeEnum.values()) {
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
				.map(PictureUploadTypeEnum::getKey)
				.collect(Collectors.toList());
	}
}
