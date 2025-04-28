package com.baolong.pictures.infrastructure.api.pictureSearch;

import cn.hutool.json.JSONUtil;
import com.baolong.pictures.infrastructure.api.pictureSearch.impl.SoSearchPicture;
import com.baolong.pictures.infrastructure.api.pictureSearch.model.SearchPictureResult;

import java.util.List;

/**
 * 以图搜图测试
 */
public class PictureSearchTest {
	public static void main(String[] args) {
		// 360以图搜图
		// String imageUrl1 = "https://baolong-picture-1259638363.cos.ap-shanghai.myqcloud.com//public/10000000/2025-02-15_lzn23PuxZqt8CPB1.";
		String imageUrl1 = "https://cos.baolong.icu/public/1903788995618312193/2025_03_24/5007f4c95f8b453e8c26d1134b6a1b8e.png";
		AbstractSearchPicture soSearchPicture = new SoSearchPicture();
		List<SearchPictureResult> soResultList = soSearchPicture.execute("so", imageUrl1, 1, 21);
		System.out.println("结果列表: " + JSONUtil.parse(soResultList));
		// // 百度以图搜图
		// String imageUrl2 = "https://www.codefather.cn/logo.png";
		// AbstractSearchPicture baiduSearchPicture = new BaiduSearchPicture();
		// List<SearchPictureResult> baiduResultList = baiduSearchPicture.execute("BAIDU", imageUrl2, 1, 31);
		// System.out.println("结果列表" + JSONUtil.parse(baiduResultList));
	}
}
