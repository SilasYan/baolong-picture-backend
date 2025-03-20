package com.baolong.pictures.infrastructure.api.imageSearch.baidu;

import com.baolong.pictures.infrastructure.api.imageSearch.baidu.model.ImageSearchResult;
import com.baolong.pictures.infrastructure.api.imageSearch.baidu.sub.GetImageFirstUrlApi;
import com.baolong.pictures.infrastructure.api.imageSearch.baidu.sub.GetImageListApi;
import com.baolong.pictures.infrastructure.api.imageSearch.baidu.sub.GetImagePageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 图片搜索接口
 * <p>
 * 这里用了 门面模式
 */
@Slf4j
public class ImageSearchApiFacade {

	/**
	 * 搜索图片
	 *
	 * @param imageUrl 需要以图搜图的图片地址
	 * @return 图片搜索结果列表
	 */
	public static List<ImageSearchResult> searchImage(String imageUrl) {
		String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
		String imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
		List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl);
		return imageList;
	}

	public static void main(String[] args) {
		// 测试以图搜图功能
		String imageUrl = "https://www.codefather.cn/logo.png";
		List<ImageSearchResult> resultList = searchImage(imageUrl);
		System.out.println("结果列表" + resultList);
	}
}
