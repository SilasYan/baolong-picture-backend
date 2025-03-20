package com.baolong.pictures.application.service;

import com.baolong.pictures.domain.space.entity.Space;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.request.space.SpaceActivateRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceAddRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceEditRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceQueryRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceUpdateRequest;
import com.baolong.pictures.interfaces.web.response.space.SpaceDetailVO;
import com.baolong.pictures.interfaces.web.response.space.SpaceLevelVO;
import com.baolong.pictures.interfaces.web.response.space.SpaceVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import java.util.List;

/**
 * 空间应用服务接口
 */
public interface SpaceApplicationService {

	// region 其他相关

	/**
	 * 校验空间上传权限
	 *
	 * @param spaceId   空间 ID
	 * @param loginUser 用户对象
	 */
	void checkSpaceUploadAuth(Long spaceId, User loginUser);

	/**
	 * 校验空间权限
	 *
	 * @param space     空间对象
	 * @param user      用户对象
	 * @param loginUser 登录用户对象
	 */
	void checkSpaceAddAuth(Space space, User user, User loginUser);

	/**
	 * 校验空间编辑权限
	 *
	 * @param space     空间对象
	 * @param loginUser 登录用户对象
	 */
	void checkSpaceChangeAuth(Space space, User loginUser);

	/**
	 * 校验空间是否存在
	 *
	 * @param spaceId 空间 ID
	 */
	void checkSpaceExisted(Long spaceId);

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param spaceQueryRequest 空间查询请求
	 * @return 查询条件对象（Lambda）
	 */
	LambdaQueryWrapper<Space> getLambdaQueryWrapper(SpaceQueryRequest spaceQueryRequest);

	// endregion 其他相关

	// region 增删改

	/**
	 * 激活空间
	 *
	 * @param spaceActivateRequest 空间激活请求
	 * @return 空间 ID
	 */
	Long activateSpace(SpaceActivateRequest spaceActivateRequest);

	/**
	 * 新增空间
	 *
	 * @param spaceAddRequest 空间新增请求
	 * @return 空间 ID
	 */
	Long addSpace(SpaceAddRequest spaceAddRequest);

	/**
	 * 删除空间
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	Boolean deleteSpace(DeleteRequest deleteRequest);

	/**
	 * 更新空间
	 *
	 * @param spaceUpdateRequest 空间更新请求
	 * @return 是否成功
	 */
	Boolean updateSpace(SpaceUpdateRequest spaceUpdateRequest);

	/**
	 * 编辑空间
	 *
	 * @param spaceEditRequest 空间编辑请求
	 * @return 是否成功
	 */
	Boolean editSpace(SpaceEditRequest spaceEditRequest);

	/**
	 * 更新空间大小和数量
	 *
	 * @param spaceId  空间 ID
	 * @param picSize  图片大小
	 * @param picCount 图片数量
	 */
	Boolean updateSpaceSizeAndCount(Long spaceId, Long picSize, Long picCount);

	// endregion 增删改

	// region 查询相关

	/**
	 * 获取登录用户的空间详情
	 *
	 * @return 登录用户的空间详情
	 */
	SpaceDetailVO getSpaceDetailByLoginUser();

	/**
	 * 根据空间 ID 获取空间信息
	 *
	 * @param spaceId 空间 ID
	 * @return 空间信息
	 */
	Space getSpaceInfoById(Long spaceId);

	/**
	 * 根据用户 ID 获取个人空间信息
	 *
	 * @param userId 用户 ID
	 * @return 空间信息
	 */
	Space getSpaceInfoByUserId(Long userId);

	/**
	 * 根据空间 ID 获取空间详情
	 *
	 * @param spaceId 空间 ID
	 * @return 空间详情
	 */
	SpaceDetailVO getSpaceVOById(Long spaceId);

	/**
	 * 获取用户空间列表
	 *
	 * @param spaceQueryRequest 空间查询请求
	 * @return 空间列表
	 */
	List<SpaceDetailVO> getSpaceListAsUser(SpaceQueryRequest spaceQueryRequest);

	/**
	 * 获取空间管理分页列表
	 *
	 * @param spaceQueryRequest 空间查询请求
	 * @return 空间管理分页列表
	 */
	PageVO<SpaceVO> getSpacePageListAsManage(SpaceQueryRequest spaceQueryRequest);

	/**
	 * 获取空间等级列表
	 *
	 * @return 空间等级列表
	 */
	List<SpaceLevelVO> getSpaceLevelList();

	// endregion 查询相关
}
