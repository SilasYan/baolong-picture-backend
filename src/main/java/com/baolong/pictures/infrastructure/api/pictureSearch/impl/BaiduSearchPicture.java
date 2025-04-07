package com.baolong.pictures.infrastructure.api.pictureSearch.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.infrastructure.api.pictureSearch.AbstractSearchPicture;
import com.baolong.pictures.infrastructure.api.pictureSearch.enums.SearchSourceEnum;
import com.baolong.pictures.infrastructure.api.pictureSearch.model.SearchPictureResult;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 百度以图搜图实现
 * <p>
 * 说明: 百度的以图搜图默认返回 30 条
 *
 * @author Silas Yan 2025-03-23:11:09
 */
@Slf4j
@Component
public class BaiduSearchPicture extends AbstractSearchPicture {
	/**
	 * 根据原图片获取搜索图片的列表地址
	 *
	 * @param searchSourceEnum 搜索源枚举
	 * @param sourcePicture    源图片
	 * @return 搜索图片的列表地址
	 */
	@Override
	protected String executeSearch(SearchSourceEnum searchSourceEnum, String sourcePicture) {
		String searchUrl = String.format(searchSourceEnum.getUrl(), System.currentTimeMillis());
		log.info("[百度搜图]搜图地址：{}", searchUrl);
		try {
			String pageUrl = getPageUrl(searchUrl, sourcePicture);
			return getListUrl(pageUrl);
		} catch (Exception e) {
			log.error("[百度搜图]搜图失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
		}
	}

	/**
	 * 发送请求获取响应
	 *
	 * @param requestUrl  请求地址
	 * @param randomSeed  随机种子
	 * @param searchCount 搜索数量
	 * @return 响应结果
	 */
	@Override
	protected List<SearchPictureResult> sendRequestGetResponse(String requestUrl, Integer randomSeed, Integer searchCount) {
		log.info("[百度搜图]搜图地址：{}, 随机种子: {}, 搜索数量: {}", requestUrl, randomSeed, searchCount);
		if (searchCount == null) searchCount = 30;
		List<SearchPictureResult> resultList = new ArrayList<>();
		int currentWhileNum = 0;
		int targetWhileNum = searchCount / 30 + 1;
		while (currentWhileNum < targetWhileNum && resultList.size() < searchCount) {
			if (randomSeed == null) randomSeed = RandomUtil.randomInt(1, 20);
			log.info("[百度搜图]当前随机种子: {}, 当前结果数量: {}", randomSeed, resultList.size());
			String URL = requestUrl + "&page=" + randomSeed;
			try (HttpResponse response = HttpUtil.createGet(URL).execute()) {
				// 判断响应状态
				if (HttpStatus.HTTP_OK != response.getStatus()) {
					log.error("[百度搜图]搜图失败，响应状态码：{}", response.getStatus());
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
				}
				// 解析响应, 处理响应结果
				JSONObject body = JSONUtil.parseObj(response.body());
				if (!body.containsKey("data")) {
					log.error("[百度搜图]搜图失败，未获取到图片数据");
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "未获取到图片列表");
				}
				JSONObject data = body.getJSONObject("data");
				if (!data.containsKey("list")) {
					log.error("[百度搜图]搜图失败，未获取到图片数据");
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "未获取到图片列表");
				}
				JSONArray baiduResult = data.getJSONArray("list");
				for (Object o : baiduResult) {
					JSONObject so = (JSONObject) o;
					SearchPictureResult pictureResult = new SearchPictureResult();
					pictureResult.setImageUrl(so.getStr("thumbUrl"));
					pictureResult.setImageKey(so.getStr("contsign"));
					resultList.add(pictureResult);
				}
				currentWhileNum++;
			} catch (Exception e) {
				log.error("[百度搜图]搜图失败", e);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
			} finally {
				randomSeed++;
			}
		}
		log.info("[百度搜图]最终结果数量: {}", resultList.size());
		return resultList;
	}

	/**
	 * 获取图片页面地址
	 *
	 * @param searchUrl     搜索地址
	 * @param sourcePicture 源图片
	 * @return 图片页面地址
	 */
	public static String getPageUrl(String searchUrl, String sourcePicture) {
		Map<String, Object> formData = new HashMap<>();
		formData.put("image", sourcePicture);
		formData.put("tn", "pc");
		formData.put("from", "pc");
		formData.put("image_source", "PC_UPLOAD_URL");
		String acsToken = "jmM4zyI8OUixvSuWh0sCy4xWbsttVMZb9qcRTmn6SuNWg0vCO7N0s6Lffec+IY5yuqHujHmCctF9BVCGYGH0H5SH/H3VPFUl4O4CP1jp8GoAzuslb8kkQQ4a21Tebge8yhviopaiK66K6hNKGPlWt78xyyJxTteFdXYLvoO6raqhz2yNv50vk4/41peIwba4lc0hzoxdHxo3OBerHP2rfHwLWdpjcI9xeu2nJlGPgKB42rYYVW50+AJ3tQEBEROlg/UNLNxY+6200B/s6Ryz+n7xUptHFHi4d8Vp8q7mJ26yms+44i8tyiFluaZAr66/+wW/KMzOhqhXCNgckoGPX1SSYwueWZtllIchRdsvCZQ8tFJymKDjCf3yI/Lw1oig9OKZCAEtiLTeKE9/CY+Crp8DHa8Tpvlk2/i825E3LuTF8EQfzjcGpVnR00Lb4/8A";
		try (HttpResponse response = HttpRequest.post(searchUrl).form(formData)
				.header("Acs-Token", acsToken).timeout(5000).execute()) {
			// 判断响应状态
			if (HttpStatus.HTTP_OK != response.getStatus()) {
				log.error("[百度搜图]搜图失败，响应状态码：{}", response.getStatus());
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
			}
			// 解析响应
			JSONObject body = JSONUtil.parseObj(response.body());
			if (!body.getInt("status").equals(0)) {
				log.error("[百度搜图]搜图失败，响应内容为空");
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
			}
			JSONObject data = JSONUtil.parseObj(body.getStr("data"));
			String rawUrl = data.getStr("url");
			if (StrUtil.isEmpty(rawUrl)) {
				log.error("[百度搜图]搜图失败，地址为空");
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
			}
			String decodeUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
			if (StrUtil.isEmpty(decodeUrl)) {
				log.error("[百度搜图]搜图失败，未获取到图片页面地址");
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
			}
			return decodeUrl;
		} catch (Exception e) {
			log.error("[百度搜图]搜图失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
		}
	}

	/**
	 * 获取图片列表地址
	 *
	 * @param resultUrl 结果页面地址
	 * @return 图片列表地址
	 */
	private static String getListUrl(String resultUrl) {
		try {
			// 使用 Jsoup 获取 HTML 内容
			Document document = Jsoup.connect(resultUrl).timeout(5000).get();
			// 获取所有 <script> 标签
			Elements scriptElements = document.getElementsByTag("script");
			// 遍历找到包含 `firstUrl` 的脚本内容
			String firstUrl = null;
			for (Element script : scriptElements) {
				String scriptContent = script.html();
				if (scriptContent.contains("\"firstUrl\"")) {
					// 正则表达式提取 firstUrl 的值
					Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*\"(.*?)\"");
					Matcher matcher = pattern.matcher(scriptContent);
					if (matcher.find()) {
						// 处理转义字符
						firstUrl = matcher.group(1).replace("\\/", "/");
					}
				}
			}
			if (StrUtil.isEmpty(firstUrl)) {
				log.error("[百度搜图]搜图失败，未找到图片元素");
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
			}
			return firstUrl;
		} catch (Exception e) {
			log.error("[百度搜图]搜图失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
		}
	}

}
