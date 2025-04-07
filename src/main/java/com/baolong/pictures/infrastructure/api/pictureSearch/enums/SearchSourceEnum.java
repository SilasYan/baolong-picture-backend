package com.baolong.pictures.infrastructure.api.pictureSearch.enums;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索来源枚举
 */
@Getter
public enum SearchSourceEnum {

	SO("so", "360", "https://st.so.com/r?src=st&srcsp=home&img_url=%s&submittype=imgurl"),
	BAIDU("baidu", "百度", "https://graph.baidu.com/upload?uptime=%s");

	private final String key;
	private final String label;
	private final String url;

	SearchSourceEnum(String key, String label, String url) {
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
	public static SearchSourceEnum of(String key) {
		if (ObjUtil.isEmpty(key)) return null;
		return ArrayUtil.firstMatch(e -> e.getKey().equals(key), values());
	}

	/**
	 * 根据 KEY 获取枚举
	 *
	 * @param key KEY
	 * @return 枚举
	 */
	public static SearchSourceEnum getEnumByKey(String key) {
		if (ObjUtil.isEmpty(key)) {
			return null;
		}
		for (SearchSourceEnum anEnum : SearchSourceEnum.values()) {
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
				.map(SearchSourceEnum::getKey)
				.collect(Collectors.toList());
	}
}
