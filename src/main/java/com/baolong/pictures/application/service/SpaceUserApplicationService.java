package com.baolong.pictures.application.service;

import com.baolong.pictures.domain.space.entity.SpaceUser;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.request.space.SpaceUserAddRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceUserEditRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceUserQueryRequest;
import com.baolong.pictures.interfaces.web.response.space.SpaceUserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

/**
 * 空间用户应用服务接口
 */
public interface SpaceUserApplicationService {

	// region 增删改相关

	/**
	 * 新增空间用户到空间
	 *
	 * @param spaceUserAddRequest 空间用户新增请求
	 * @return 是否成功
	 */
	Boolean addSpaceUserToSpace(SpaceUserAddRequest spaceUserAddRequest);

	/**
	 * 编辑空间用户权限
	 *
	 * @param spaceUserEditRequest 空间用户编辑请求
	 * @return 是否成功
	 */
	Boolean editSpaceUserAuth(SpaceUserEditRequest spaceUserEditRequest);

	/**
	 * 删除空间用户
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	Boolean deleteSpaceUser(DeleteRequest deleteRequest);

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 根据空间 ID 查询空间用户信息
	 *
	 * @param spaceUserId 空间用户 ID
	 * @return 空间用户信息
	 */
	SpaceUser getSpaceUserById(Long spaceUserId);

	/**
	 * 根据空间 ID 查询空间用户分页列表
	 *
	 * @param spaceUserQueryRequest 空间用户查询请求
	 * @return 空间用户分页列表
	 */
	PageVO<SpaceUserVO> getSpaceUserPageListBySpaceId(SpaceUserQueryRequest spaceUserQueryRequest);

	// endregion 查询相关

	// region 其他相关

	/**
	 * 校验空间用户权限
	 *
	 * @param spaceId   空间 ID
	 * @param loginUser 用户对象
	 */
	void checkSpaceUserAuth(Long spaceId, User loginUser);

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param spaceUserQueryRequest 空间用户查询请求
	 * @return 查询条件对象（Lambda）
	 */
	LambdaQueryWrapper<SpaceUser> getLambdaQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest);

	// endregion 其他相关
}
