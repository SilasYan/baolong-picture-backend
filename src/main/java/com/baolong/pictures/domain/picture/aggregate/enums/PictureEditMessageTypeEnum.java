package com.baolong.pictures.domain.picture.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 图片编辑消息类型枚举
 *
 * @author Silas Yan 2025-05-06 19:46
 */
@Getter
public enum PictureEditMessageTypeEnum {

	INFO("INFO", "发送通知"),
	ERROR("ERROR", "发送错误"),
	ENTER_EDIT("ENTER_EDIT", "进入编辑状态"),
	EXIT_EDIT("EXIT_EDIT", "退出编辑状态"),
	EDIT_ACTION("EDIT_ACTION", "执行编辑操作"),
	COMPLETE("COMPLETE", "操作完成");

	private final String key;

	private final String label;

	PictureEditMessageTypeEnum(String key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static PictureEditMessageTypeEnum of(String key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static PictureEditMessageTypeEnum getEnumByKey(String key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (PictureEditMessageTypeEnum anEnum : PictureEditMessageTypeEnum.values()) {
			if (anEnum.key.equals(key)) {
				return anEnum;
			}
		}
		return null;
	}
}
