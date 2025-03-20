package com.baolong.pictures.infrastructure.api.imageSearch.so.sub;

import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * 获取360搜图的图片的接口
 *
 * @author Baolong 2025年02月19 22:54
 * @version 1.0
 * @since 1.8
 */
@Slf4j
public class GetSoImageUrlApi {
	public static String getSoImageUrl(String imageUrl) {
		String url = "https://st.so.com/r?src=st&srcsp=home&img_url=" + imageUrl + "&submittype=imgurl";
		try {
			Document document = Jsoup.connect(url).timeout(5000).get();
			Element imgElement = document.selectFirst(".img_img");
			if (imgElement != null) {
				String soImageUrl = "";
				// 获取当前元素的属性
				String style = imgElement.attr("style");
				if (style.contains("background-image:url(")) {
					// 提取URL部分
					int start = style.indexOf("url(") + 4;  // 从"Url("之后开始
					int end = style.indexOf(")", start);    // 找到右括号的位置
					if (start > 4 && end > start) {
						soImageUrl = style.substring(start, end);
					}
				}
				return soImageUrl;
			}
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
		} catch (Exception e) {
			log.error("搜图失败", e);
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜图失败");
		}
	}

	public static void main(String[] args) {
		String imageUrl = "https://baolong-picture-1259638363.cos.ap-shanghai.myqcloud.com//public/10000000/2025-02-15_ILJxljPdt9Kv1EM1.";
		String result = getSoImageUrl(imageUrl);
		System.out.println("搜索成功，结果 URL：" + result);
	}
}
