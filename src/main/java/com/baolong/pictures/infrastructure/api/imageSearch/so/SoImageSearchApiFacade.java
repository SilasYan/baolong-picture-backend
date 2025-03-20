package com.baolong.pictures.infrastructure.api.imageSearch.so;

import com.baolong.pictures.infrastructure.api.imageSearch.so.model.SoImageSearchResult;
import com.baolong.pictures.infrastructure.api.imageSearch.so.sub.GetSoImageListApi;
import com.baolong.pictures.infrastructure.api.imageSearch.so.sub.GetSoImageUrlApi;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 360搜图图片搜索接口
 * <p>
 * 这里用了 门面模式
 */
@Slf4j
public class SoImageSearchApiFacade {

	/**
	 * 搜索图片
	 *
	 * @param imageUrl 需要以图搜图的图片地址
	 * @param start    开始下表
	 * @return 图片搜索结果列表
	 */
	public static List<SoImageSearchResult> searchImage(String imageUrl, Integer start) {
		String soImageUrl = GetSoImageUrlApi.getSoImageUrl(imageUrl);
		List<SoImageSearchResult> imageList = GetSoImageListApi.getImageList(soImageUrl, start);
		return imageList;
	}

	public static void main(String[] args) {
		// 测试以图搜图功能
		String imageUrl = "https://baolong-picture-1259638363.cos.ap-shanghai.myqcloud.com//public/10000000/2025-02-15_lzn23PuxZqt8CPB1.";
		List<SoImageSearchResult> resultList = searchImage(imageUrl, 0);
		System.out.println("结果列表" + resultList);
	}
}
