package com.baolong.pictures.domain.system.task.aggregate.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务状态枚举
 */
@Getter
public enum TaskStatusEnum {

	CLOSE(0, "关闭"),
	OPEN(1, "开启");

	private final Integer key;
	private final String label;

	TaskStatusEnum(Integer key, String label) {
		this.key = key;
		this.label = label;
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key 状态键值
	 * @return 枚举对象，未找到时返回 null
	 */
	public static TaskStatusEnum of(Integer key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static TaskStatusEnum getEnumByKey(Integer key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (TaskStatusEnum anEnum : TaskStatusEnum.values()) {
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
				.map(TaskStatusEnum::getKey)
				.collect(Collectors.toList());
	}

	/**
	 * 判断是否开启
	 *
	 * @param key 状态键值
	 * @return 是否开启
	 */
	public static boolean isOpen(Integer key) {
		return OPEN.getKey().equals(key);
	}
}
