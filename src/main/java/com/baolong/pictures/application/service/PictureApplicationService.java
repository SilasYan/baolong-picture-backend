package com.baolong.pictures.application.service;

import com.baolong.pictures.domain.picture.entity.Picture;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.api.grab.model.GrabPictureResult;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.request.picture.PictureBatchEditRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureEditRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureGrabRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureInteractionRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureQueryRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureReviewRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureUpdateRequest;
import com.baolong.pictures.interfaces.web.request.picture.PictureUploadRequest;
import com.baolong.pictures.interfaces.web.response.picture.PictureDetailVO;
import com.baolong.pictures.interfaces.web.response.picture.PictureHomeVO;
import com.baolong.pictures.interfaces.web.response.picture.PictureVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * 图片应用服务接口
 */
public interface PictureApplicationService {

	// region 其他方法

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 查询条件对象（Lambda）
	 */
	LambdaQueryWrapper<Picture> getLambdaQueryWrapper(PictureQueryRequest pictureQueryRequest);

	/**
	 * 校验并填充审核参数
	 *
	 * @param picture   图片对象
	 * @param loginUser 登录用户
	 */
	void checkAndFillReviewParams(Picture picture, User loginUser);

	/**
	 * 校验图片操作权限
	 *
	 * @param picture   图片对象
	 * @param loginUser 登录用户
	 */
	void checkPictureChangeAuth(Picture picture, User loginUser);

	/**
	 * 填充图片名称规则
	 *
	 * @param pictureList 图片列表
	 * @param nameRule    名称规则
	 */
	void fillPictureNameRuleBatch(List<Picture> pictureList, String nameRule);

	// endregion 其他方法

	// region 增删改相关（包含上传图片）

	/**
	 * 上传图片
	 *
	 * @param pictureInputSource   图片输入源
	 * @param pictureUploadRequest 图片上传请求
	 * @return PictureVO
	 */
	PictureDetailVO uploadPicture(Object pictureInputSource, PictureUploadRequest pictureUploadRequest);

	/**
	 * 删除图片
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	Boolean deletePicture(DeleteRequest deleteRequest);

	/**
	 * 更新图片
	 *
	 * @param pictureUpdateRequest 图片更新请求
	 * @return 是否成功
	 */
	Boolean updatePicture(PictureUpdateRequest pictureUpdateRequest);

	/**
	 * 编辑图片
	 *
	 * @param pictureEditRequest 图片编辑请求
	 * @return 是否成功
	 */
	Boolean editPicture(PictureEditRequest pictureEditRequest);

	/**
	 * 编辑图片（批量）
	 *
	 * @param pictureBatchEditRequest 图片批量编辑请求
	 * @return 是否成功
	 */
	Boolean editPictureBatch(PictureBatchEditRequest pictureBatchEditRequest);

	/**
	 * 审核图片
	 *
	 * @param pictureReviewRequest 图片审核请求
	 * @return 是否成功
	 */
	Boolean reviewPicture(PictureReviewRequest pictureReviewRequest);

	// endregion 增删改相关（包含上传图片）

	// region 查询相关

	/**
	 * 根据图片 ID 获取图片信息
	 *
	 * @param pictureId 图片 ID
	 * @return 图片信息
	 */
	Picture getPictureInfoById(Long pictureId);

	/**
	 * 根据图片 ID 获取图片详情
	 *
	 * @param pictureId 图片 ID
	 * @return 图片详情
	 */
	PictureDetailVO getPictureDetailById(Long pictureId);

	/**
	 * 获取首页图片列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 首页图片列表
	 */
	PageVO<PictureHomeVO> getPicturePageListAsHome(PictureQueryRequest pictureQueryRequest);

	/**
	 * 获取图片管理分页列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 图片管理分页列表
	 */
	PageVO<PictureVO> getPicturePageListAsManage(PictureQueryRequest pictureQueryRequest);

	/**
	 * 获取个人空间图片分页列表
	 *
	 * @param pictureQueryRequest 图片查询请求
	 * @return 个人空间图片分页列表
	 */
	PageVO<PictureVO> getPicturePageListAsPersonSpace(PictureQueryRequest pictureQueryRequest);

	// /**
	//  * 根据颜色搜索图片
	//  *
	//  * @param pictureColorSearchRequest 图片颜色搜索请求
	//  * @return 图片列表
	//  */
	// List<PictureVO> searchPictureByColor(PictureColorSearchRequest pictureColorSearchRequest);

	/**
	 * 爬取图片
	 *
	 * @param pictureGrabRequest 图片抓取请求
	 * @return 爬取的图片列表
	 */
	List<GrabPictureResult> grabPicture(PictureGrabRequest pictureGrabRequest);

	// endregion 查询相关

	/**
	 * 图片下载
	 *
	 * @param pictureId 图片 ID
	 * @return 原图地址
	 */
	String pictureDownload(Long pictureId);

	/**
	 * 图片分享
	 *
	 * @param pictureId 图片 ID
	 * @return true
	 */
	Boolean pictureShare(Long pictureId);

	/**
	 * 图片查看
	 *
	 * @param pictureId 图片 ID
	 */
	@Async
	void pictureView(Long pictureId);

	/**
	 * 图片点赞或收藏
	 *
	 * @param pictureInteractionRequest 图片互动请求
	 * @return true
	 */
	Boolean pictureLikeOrCollect(PictureInteractionRequest pictureInteractionRequest);

	//
	// /**
	//  * 创建图片扩图任务
	//  *
	//  * @param createPictureOutPaintingTaskRequest 图片扩图任务请求
	//  * @param loginUser                           当前登录用户
	//  * @return 扩图任务响应
	//  */
	// CreateOutPaintingTaskResponse createPictureOutPaintingTask(CreatePictureOutPaintingTaskRequest createPictureOutPaintingTaskRequest, User loginUser);
	//
	// /**
	//  * 获取图片指定字段列表
	//  *
	//  * @param queryWrapper 查询条件
	//  * @return 图片指定字段的列表
	//  */
	// List<Object> getPictureAppointFieldList(QueryWrapper<TePicture> queryWrapper);
	//
	// /**
	//  * 获取图片指定字段 Map 列表
	//  *
	//  * @param queryWrapper 查询条件
	//  * @return 图片指定字段的列表
	//  */
	// List<Map<String, Object>> getPictureAppointFieldMaps(QueryWrapper<TePicture> queryWrapper);

}
