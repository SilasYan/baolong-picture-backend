package com.baolong.pictures.infrastructure.api.imageSearch.baidu.sub;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取以图搜图页面地址
 * <p>
 * 这里使用的是 百度的以图搜图
 */
@Slf4j
public class GetImagePageUrlApi {

	/**
	 * 获取图片页面地址
	 *
	 * @param imageUrl 图片地址
	 * @return 图片页面地址
	 */
	public static String getImagePageUrl(String imageUrl) {
		// 1. 准备请求参数
		Map<String, Object> formData = new HashMap<>();
		formData.put("image", imageUrl);
		formData.put("tn", "pc");
		formData.put("from", "pc");
		formData.put("image_source", "PC_UPLOAD_URL");
		// 获取当前时间戳
		long uptime = System.currentTimeMillis();
		// 请求地址
		String url = "https://graph.baidu.com/upload?uptime=" + uptime;
		String acsToken = "jmM4zyI8OUixvSuWh0sCy4xWbsttVMZb9qcRTmn6SuNWg0vCO7N0s6Lffec+IY5yuqHujHmCctF9BVCGYGH0H5SH/H3VPFUl4O4CP1jp8GoAzuslb8kkQQ4a21Tebge8yhviopaiK66K6hNKGPlWt78xyyJxTteFdXYLvoO6raqhz2yNv50vk4/41peIwba4lc0hzoxdHxo3OBerHP2rfHwLWdpjcI9xeu2nJlGPgKB42rYYVW50+AJ3tQEBEROlg/UNLNxY+6200B/s6Ryz+n7xUptHFHi4d8Vp8q7mJ26yms+44i8tyiFluaZAr66/+wW/KMzOhqhXCNgckoGPX1SSYwueWZtllIchRdsvCZQ8tFJymKDjCf3yI/Lw1oig9OKZCAEtiLTeKE9/CY+Crp8DHa8Tpvlk2/i825E3LuTF8EQfzjcGpVnR00Lb4/8A";

		try {
			// 2. 发送 POST 请求到百度接口
			HttpResponse response = HttpRequest.post(url)
					.form(formData)
					.header("Acs-Token", acsToken)
					.timeout(5000)
					.execute();
			// 判断响应状态
			if (HttpStatus.HTTP_OK != response.getStatus()) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
			}
			// 解析响应
			String responseBody = response.body();
			System.out.println("响应结果: " + responseBody);
			Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);

			// 3. 处理响应结果
			if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
			}
			Map<String, Object> data = (Map<String, Object>) result.get("data");
			String rawUrl = (String) data.get("url");
			// 对 URL 进行解码
			String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
			// 如果 URL 为空
			if (searchResultUrl == null) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效结果");
			}
			return searchResultUrl;
		} catch (Exception e) {
			log.error("搜索失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
		}
	}

	public static void main(String[] args) {
		/*// 测试以图搜图功能
		String imageUrl = "https://www.codefather.cn/logo.png";
		String result = getImagePageUrl(imageUrl);
		System.out.println("搜索成功，结果 URL：" + result);*/

		// Step 1
		String imgUrl = "https://baolong-picture-1259638363.cos.ap-shanghai.myqcloud.com//public/10000000/2025-02-15_ILJxljPdt9Kv1EM1.";
		String url = "https://st.so.com/r?src=st&srcsp=home&img_url=" + imgUrl + "&submittype=imgurl";
		String imageUrl = "";
		try {
			Document document = Jsoup.connect(url).timeout(5000).get();
			Element imgElement = document.selectFirst(".img_img");
			if (imgElement != null) {
				String style = imgElement.attr("style");
				imageUrl = extractImageUrl(style);
				System.out.println(imageUrl);

			} else {
				System.out.println("未找到 img 元素");
			}
			System.out.println(imgElement);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Step 2
		String reqUrl2 = "https://st.so.com/stu?a=mrecomm&start=0";
		Map<String, Object> formData = new HashMap<>();
		formData.put("img_url", imageUrl);
		HttpResponse response2 = HttpRequest.post(reqUrl2)
				.form(formData)
				.timeout(5000)
				.execute();
		// 判断响应状态
		if (HttpStatus.HTTP_OK != response2.getStatus()) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
		}
		// 解析响应
		String responseBody = response2.body();
		// System.out.println("响应结果: "+responseBody);
		Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);
		// 3. 处理响应结果
		if (result == null || !Integer.valueOf(0).equals(result.get("errno"))) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "接口调用失败");
		}
		Map<String, Object> data = (Map<String, Object>) result.get("data");
		System.out.println("数据: " + data);
		// data.get("result")

	}

	// 提取URL的函数
	private static String extractImageUrl(String style) {
		if (style != null && style.contains("background-image:url(")) {
			// 提取URL部分
			int start = style.indexOf("url(") + 4;  // 从"Url("之后开始
			int end = style.indexOf(")", start);    // 找到右括号的位置
			if (start > 4 && end > start) {
				return style.substring(start, end); // 返回图片URL
			}
		}
		return null; // 如果找不到URL
	}
}
