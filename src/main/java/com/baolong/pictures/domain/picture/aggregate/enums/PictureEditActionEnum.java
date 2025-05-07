package com.baolong.pictures.domain.picture.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 图片编辑操作枚举
 *
 * @author Silas Yan 2025-05-06 19:46
 */
@Getter
public enum PictureEditActionEnum {

	ZOOM_IN("ZOOM_IN", "放大操作"),
	ZOOM_OUT("ZOOM_OUT", "缩小操作"),
	ROTATE_LEFT("ROTATE_LEFT", "左旋操作"),
	ROTATE_RIGHT("ROTATE_RIGHT", "右旋操作");

	private final String key;

	private final String label;

	PictureEditActionEnum(String key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static PictureEditActionEnum of(String key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static PictureEditActionEnum getEnumByKey(String key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (PictureEditActionEnum anEnum : PictureEditActionEnum.values()) {
			if (anEnum.key.equals(key)) {
				return anEnum;
			}
		}
		return null;
	}
}
