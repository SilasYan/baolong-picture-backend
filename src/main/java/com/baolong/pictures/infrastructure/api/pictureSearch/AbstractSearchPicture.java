package com.baolong.pictures.infrastructure.api.pictureSearch;

import com.baolong.pictures.infrastructure.api.pictureSearch.enums.SearchSourceEnum;
import com.baolong.pictures.infrastructure.api.pictureSearch.model.SearchPictureResult;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 以图搜图
 *
 * @author Silas Yan 2025-03-23:09:50
 */
@Slf4j
public abstract class AbstractSearchPicture {

	/**
	 * 执行搜索
	 *
	 * @param searchSource  搜索源
	 * @param sourcePicture 源图片
	 * @param randomSeed    随机种子
	 * @param searchCount   搜索数量
	 * @return 搜索结果
	 */
	public final List<SearchPictureResult> execute(String searchSource, String sourcePicture, Integer randomSeed, Integer searchCount) {
		log.info("开始搜索图片，搜索源：{}，源图片：{}，随机种子：{}", searchSource, sourcePicture, randomSeed);
		// 校验
		SearchSourceEnum searchSourceEnum = SearchSourceEnum.getEnumByKey(searchSource);
		if (searchSourceEnum == null) {
			throw new BusinessException(ErrorCode.DATA_ERROR, "不支持的搜索源");
		}
		// 执行搜索
		String requestUrl = this.executeSearch(searchSourceEnum, sourcePicture);
		List<SearchPictureResult> pictureResultList = this.sendRequestGetResponse(requestUrl, randomSeed, searchCount);
		// 如果当前结果大于 searchCount 就截取
		if (pictureResultList.size() > searchCount) {
			pictureResultList = pictureResultList.subList(0, searchCount);
		}
		log.info("搜索图片结束，返回结果数量：{}", pictureResultList.size());
		return pictureResultList;
	}

	/**
	 * 根据原图片获取搜索图片的列表地址
	 *
	 * @param searchSourceEnum 搜索源枚举
	 * @param sourcePicture    源图片
	 * @return 搜索图片的列表地址
	 */
	protected abstract String executeSearch(SearchSourceEnum searchSourceEnum, String sourcePicture);

	/**
	 * 发送请求获取响应
	 *
	 * @param requestUrl  请求地址
	 * @param randomSeed  随机种子
	 * @param searchCount 搜索数量
	 * @return 响应结果
	 */
	protected abstract List<SearchPictureResult> sendRequestGetResponse(String requestUrl, Integer randomSeed, Integer searchCount);
}
