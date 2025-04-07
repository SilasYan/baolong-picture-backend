package com.baolong.pictures.domain.system.feedback.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户反馈类型枚举
 */
@Getter
public enum FeedbackTypeEnum {

	EXPERIENCE(0, "使用体验"),
	SUGGESTION(1, "功能建议"),
	BUG(2, "BUG错误"),
	OTHER(3, "其他"),
	;

	private final Integer key;
	private final String label;

	FeedbackTypeEnum(Integer key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static FeedbackTypeEnum of(Integer key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static FeedbackTypeEnum getEnumByKey(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (FeedbackTypeEnum anEnum : FeedbackTypeEnum.values()) {
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
				.map(FeedbackTypeEnum::getKey)
				.collect(Collectors.toList());
	}
}
