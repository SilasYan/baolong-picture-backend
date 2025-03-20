package com.baolong.pictures.infrastructure.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 邮箱工具类
 *
 * @author Baolong 2025年03月06 22:09
 * @version 1.0
 * @since 1.8
 */
public class EmailUtils {
	public static void main(String[] args) {
		Map<String, Object> map = new HashMap<>();
		map.put("code", 1111);
		System.out.println(emailContentTemplate("templates/EmailCodeTemplate.html", "BOOT_", "_END", map));
		System.out.println(emailContentTemplate("templates/EmailCodeTemplate.html", map));
	}

	/**
	 * 发送静态模板邮箱
	 *
	 * @param path 模板路径，模板需要放在resource下。
	 * @return 内容
	 */
	public static String emailContentTemplate(String path) {
		// 读取邮件模板，该模板放在 templates/ 目录下，模板名为：EmailTemplate.html
		Resource resource = new ClassPathResource(path);
		InputStream inputStream = null;
		BufferedReader fileReader = null;
		StringBuilder buffer = new StringBuilder();
		String line = "";
		try {
			inputStream = resource.getInputStream();
			fileReader = new BufferedReader(new InputStreamReader(inputStream));
			while ((line = fileReader.readLine()) != null) {
				buffer.append(line);
			}
		} catch (Exception e) {
			throw new RuntimeException("邮件模板读取失败", e);
		} finally {
			try {
				if (fileReader != null) {
					fileReader.close();
				}
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// 替换html模板中的参数
		return buffer.toString();
	}

	/**
	 * 发送动态模板邮箱
	 *
	 * @param path     模板路径，模板需要放在resource下。
	 * @param paramMap 参数列表，按照Map中的Key进行替换。
	 * @return 内容
	 */
	public static String emailContentTemplate(String path, Map<String, Object> paramMap) {
		return emailContentTemplate(path, "BOOT_", "_END", paramMap);
	}

	/**
	 * 发送动态模板邮箱
	 *
	 * @param path     模板路径，模板需要放在resource下。
	 * @param prefix   前缀，需要替换的占位符。
	 * @param suffix   后缀，需要替换的占位符。
	 * @param paramMap 参数列表，按照Map中的Key进行替换。
	 * @return 内容
	 */
	public static String emailContentTemplate(String path, String prefix, String suffix, Map<String, Object> paramMap) {
		String str = emailContentTemplate(path);
		List<String> targetList = getTargetString(str, prefix, suffix);
		if (targetList != null) {
			for (String tl : targetList) {
				Object o = paramMap.get(tl);
				if (o != null) {
					String s = prefix + tl + suffix;
					str = str.replaceAll(s, o.toString());
				} else {
					throw new RuntimeException("邮箱模板中不存在占位字符!");
				}
			}
		}
		return str;
	}

	/**
	 * 获取占位符字符
	 *
	 * @param str      原字符串
	 * @param startStr 开始字符串
	 * @param endStr   结束字符串
	 * @return String[]
	 */
	private static List<String> getTargetString(String str, String startStr, String endStr) {
		// 获取头占位符
		List<Integer> startStrIndex = getTargetIndex(str, 0, startStr);
		// 获取尾占位符
		List<Integer> endStrIndex = getTargetIndex(str, 0, endStr);
		if (!startStrIndex.isEmpty() && !endStrIndex.isEmpty() && startStrIndex.size() != endStrIndex.size()) {
			return null;
		}
		List<String> strList = new ArrayList<>();
		for (int i = 0, num = startStrIndex.size(); i < num; i++) {
			strList.add(str.substring((startStrIndex.get(i) + startStr.length()), (endStrIndex.get(i))));
		}
		return strList;
	}

	/**
	 * 获取占位字符的下标
	 *
	 * @param string  字符串
	 * @param index   下标
	 * @param findStr 指定字符串
	 * @return List<Integer>
	 */
	private static List<Integer> getTargetIndex(String string, int index, String findStr) {
		List<Integer> list = new ArrayList<>();
		if (index != -1) {
			int num = string.indexOf(findStr, index);
			if (num == -1) {
				return list;
			}
			list.add(num);
			// 递归进行查找
			list.addAll(getTargetIndex(string, string.indexOf(findStr, num + 1), findStr));
		}
		return list;
	}
}
