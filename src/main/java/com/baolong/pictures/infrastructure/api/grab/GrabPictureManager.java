package com.baolong.pictures.infrastructure.api.grab;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.infrastructure.api.grab.model.GrabPictureResult;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 爬取图片
 *
 * @author Baolong 2025年03月15 23:04
 * @version 1.0
 * @since 1.8
 */
@Slf4j
@Component
public class GrabPictureManager {

	public static void main(String[] args) throws IOException {
		GrabPictureManager grabPictureManager = new GrabPictureManager();
		List<GrabPictureResult> grabPictureResults = grabPictureManager.grabPictureByBing("BING", "头像", 1, 12);
		System.out.println(JSONUtil.parse(grabPictureResults));
		// Document document = Jsoup.connect("https://c-ssl.dtstatic.com/uploads/item/201901/13/20190113115208_PP8yF.jpeg").get();
		// Document document = Jsoup.connect("https://c-ssl.dtstatic.com/uploads/blog/202203/22/20220322193453_8997f.jpg").get();
		// System.out.println(document);
		boolean urlAccessible = grabPictureManager.isUrlAccessible("https://c-ssl.dtstatic.com/uploads/item/201901/13/20190113115208_PP8yF.jpeg");
		System.out.println(urlAccessible);
		boolean urlAccessible2 = grabPictureManager.isUrlAccessible("https://c-ssl.dtstatic.com/uploads/blog/202203/22/20220322193453_8997f.jpg");
		System.out.println(urlAccessible2);

		// 生成 10 到 20 的随机数
		Integer randomSeed = RandomUtil.randomInt(10, 20);
	}

	/**
	 * 允许上传的图片格式
	 */
	final List<String> ALLOW_FORMAT_LIST = Arrays.asList("jpeg", "jpg", "png", "webp", "gif",
			"image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif");

	public List<GrabPictureResult> grabPictureByBing(String grabSource, String keyword, Integer randomSeed, Integer count) {
		log.info("Bing 爬取图片，keyword: {}, randomSeed: {}, count: {}", keyword, randomSeed, count);
		String fetchUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1&first=%s&count=%s", keyword, randomSeed, count);
		log.info("fetchUrl: {}", fetchUrl);
		try {
			Document document = Jsoup.connect(fetchUrl).get();
			Element div = document.getElementsByClass("dgControl").first();
			if (ObjUtil.isNull(div)) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取元素失败");
			}
			Elements imgElementList = div.select(".iusc");
			List<GrabPictureResult> grabPictureResultList = new ArrayList<>();
			for (Element imgElement : imgElementList) {
				try {
					String dataM = imgElement.attr("m");
					JSONObject entries = JSONUtil.parseObj(dataM);
					String originUrl = entries.getStr("murl");
					String thumbnailUrl = entries.getStr("turl");
					if (StrUtil.isEmpty(originUrl) && StrUtil.isEmpty(thumbnailUrl)) {
						continue;
					}
					// 处理图片上传地址，防止出现转义问题
					// int questionMarkIndex = originUrl.indexOf("?");
					// if (questionMarkIndex > -1) {
					// 	originUrl = originUrl.substring(0, questionMarkIndex);
					// }
					GrabPictureResult grabPictureResult = new GrabPictureResult();
					if (StrUtil.isNotEmpty(originUrl)) {
						grabPictureResult.setImageUrl(originUrl);
					} else {
						grabPictureResult.setImageUrl(thumbnailUrl);
					}
					// // 处理 imageUrl 的参数
					// String imageUrl = grabPictureResult.getImageUrl();
					// int thumbQMIndex = imageUrl.indexOf("?");
					// if (thumbQMIndex > -1) {
					// 	imageUrl = imageUrl.substring(0, thumbQMIndex);
					// }
					// String handleImageUrl = imageUrl.replaceAll("\\.thumb\\.\\d+_\\d+", "");

					/**
					 * 这里前端也暂时使用这个字段, 原图字段有些网站做了防盗链机制, 所以有问题
					 */
					grabPictureResult.setHandleImageUrl(thumbnailUrl);
					grabPictureResultList.add(grabPictureResult);

				} catch (Exception e) {
					log.error("解析图片数据失败", e);
				}
			}
			return grabPictureResultList;
		} catch (IOException e) {
			log.error("爬取源页面失败: ", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "爬取源页面失败");
		}
	}

	// 新增方法：检查URL是否可访问（状态码200）
	private boolean isUrlAccessible(String url) {
		if (StrUtil.isEmpty(url)) return false;
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("HEAD");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
			connection.setConnectTimeout(5000); // 5秒超时
			connection.setReadTimeout(5000);
			int responseCode = connection.getResponseCode();
			return responseCode == HttpURLConnection.HTTP_OK;
		} catch (IOException e) {
			return false; // 超时、拒绝连接等情况视为不可访问
		}
	}
}
