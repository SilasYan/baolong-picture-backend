package com.baolong.pictures.infrastructure.api.pictureSearch.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 360以图搜图实现
 * <p>
 * 说明: 360的以图搜图默认返回 20 条
 *
 * @author Silas Yan 2025-03-23:10:05
 */
@Slf4j
@Component
public class SoSearchPicture extends AbstractSearchPicture {
	/**
	 * 根据原图片获取搜索图片的列表地址
	 *
	 * @param searchSourceEnum 搜索源枚举
	 * @param sourcePicture    源图片
	 * @return 搜索图片的列表地址
	 */
	@Override
	protected String executeSearch(SearchSourceEnum searchSourceEnum, String sourcePicture) {
		String searchUrl = String.format(searchSourceEnum.getUrl(), sourcePicture);
		log.info("[360搜图]搜图地址：{}", searchUrl);
		try {
			Document document = Jsoup.connect(searchUrl).timeout(5000).get();
			Element element = document.selectFirst(".img_img");
			if (element == null) {
				log.error("[360搜图]搜图失败，未找到图片元素");
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
			}
			String imagesUrl = "";
			// 获取当前元素的属性
			String style = element.attr("style");
			if (style.contains("background-image:url(")) {
				// 提取URL部分
				int start = style.indexOf("url(") + 4;  // 从"Url("之后开始
				int end = style.indexOf(")", start);    // 找到右括号的位置
				if (start > 4 && end > start) {
					imagesUrl = style.substring(start, end);
				}
			}
			if (StrUtil.isEmpty(imagesUrl)) {
				log.error("[360搜图]搜图失败，未找到图片地址");
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
			}
			return imagesUrl;
		} catch (Exception e) {
			log.error("[360搜图]搜图失败", e);
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
		log.info("[360搜图]搜图地址：{}, 随机种子: {}, 搜索数量: {}", requestUrl, randomSeed, searchCount);
		if (searchCount == null) searchCount = 20;
		List<SearchPictureResult> resultList = new ArrayList<>();
		int currentWhileNum = 0;
		int targetWhileNum = searchCount / 20 + 1;
		while (currentWhileNum < targetWhileNum && resultList.size() < searchCount) {
			if (randomSeed == null) randomSeed = RandomUtil.randomInt(1, 20);
			log.info("[360搜图]当前随机种子: {}, 当前结果数量: {}", randomSeed, resultList.size());
			String URL = "https://st.so.com/stu?a=mrecomm&start=" + randomSeed;
			Map<String, Object> formData = new HashMap<>();
			formData.put("img_url", requestUrl);
			try (HttpResponse response = HttpRequest.post(URL).form(formData).timeout(5000).execute()) {
				// 判断响应状态
				if (HttpStatus.HTTP_OK != response.getStatus()) {
					log.error("[360搜图]搜图失败，响应状态码：{}", response.getStatus());
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
				}
				// 解析响应, 处理响应结果
				JSONObject body = JSONUtil.parseObj(response.body());
				if (!Integer.valueOf(0).equals(body.getInt("errno"))) {
					throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
				}
				JSONObject data = body.getJSONObject("data");
				JSONArray soResult = data.getJSONArray("result");
				for (Object o : soResult) {
					JSONObject so = (JSONObject) o;
					SearchPictureResult pictureResult = new SearchPictureResult();
					String prefix;
					if (StrUtil.isNotBlank(so.getStr("https"))) {
						prefix = "https://" + so.getStr("https") + "/";
					} else {
						prefix = "http://" + so.getStr("http") + "/";
					}
					pictureResult.setImageUrl(prefix + so.getStr("imgkey"));
					pictureResult.setImageName(so.getStr("title"));
					pictureResult.setImageKey(so.getStr("imgkey"));
					resultList.add(pictureResult);
				}
				currentWhileNum++;
			} catch (Exception e) {
				log.error("[360搜图]搜图失败", e);
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
			} finally {
				randomSeed++;
			}
		}
		log.info("[360搜图]最终结果数量: {}", resultList.size());
		return resultList;
	}
}
