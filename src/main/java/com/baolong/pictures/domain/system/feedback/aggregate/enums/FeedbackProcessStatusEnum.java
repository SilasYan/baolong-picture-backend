package com.baolong.pictures.domain.system.feedback.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户反馈处理状态枚举
 */
@Getter
public enum FeedbackProcessStatusEnum {

	PENDING(0, "待处理"),
	PROCESSING(1, "处理中"),
	COMPLETED(2, "完成"),
	REJECTED(3, "拒绝"),
	;

	private final Integer key;
	private final String label;

	FeedbackProcessStatusEnum(Integer key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static FeedbackProcessStatusEnum of(Integer key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static FeedbackProcessStatusEnum getEnumByKey(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (FeedbackProcessStatusEnum anEnum : FeedbackProcessStatusEnum.values()) {
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
				.map(FeedbackProcessStatusEnum::getKey)
				.collect(Collectors.toList());
	}
}
