package com.baolong.pictures.domain.space.service;

import com.baolong.pictures.domain.space.entity.Space;
import com.baolong.pictures.interfaces.dto.space.SpaceQueryRequest;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 空间表 (space) - 领域服务接口
 */
public interface SpaceDomainService extends IService<Space> {

	// region 其他相关

	/**
	 * 根据用户 ID 和空间类型判断空间是否存在
	 *
	 * @param userId    用户 ID
	 * @param spaceType 空间类型
	 * @return 是否存在
	 */
	Boolean existSpaceByUserIdAndSpaceType(Long userId, Integer spaceType);

	/**
	 * 根据空间 ID 判断空间是否存在
	 *
	 * @param spaceId 空间 ID
	 * @return 是否存在
	 */
	Boolean existSpaceById(Long spaceId);

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param spaceQueryRequest 空间查询请求
	 * @return 查询条件对象（Lambda）
	 */
	LambdaQueryWrapper<Space> getLambdaQueryWrapper(SpaceQueryRequest spaceQueryRequest);

	// endregion 其他相关

	// region 增删改相关

	/**
	 * 创建空间
	 *
	 * @param space 空间对象
	 * @return 是否成功
	 */
	Boolean addSpace(Space space);

	/**
	 * 删除空间
	 *
	 * @param spaceId 空间ID
	 * @return 是否成功
	 */
	Boolean deleteSpace(Long spaceId);

	/**
	 * 更新空间
	 *
	 * @param space 空间对象
	 * @return 是否成功
	 */
	Boolean updateSpace(Space space);

	/**
	 * 更新空间大小和数量
	 *
	 * @param updateWrapper 更新条件
	 * @return 是否成功
	 */
	Boolean updateSpaceSizeAndCount(LambdaUpdateWrapper<Space> updateWrapper);

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 根据空间 ID 获取空间信息
	 *
	 * @param spaceId 空间 ID
	 * @return 空间信息
	 */
	Space getSpaceById(Long spaceId);

	/**
	 * 获取用户空间列表
	 *
	 * @param lambdaQueryWrapper 查询条件
	 * @return 空间列表
	 */
	List<Space> getSpaceListAsUser(LambdaQueryWrapper<Space> lambdaQueryWrapper);

	/**
	 * 获取空间管理分页列表
	 *
	 * @param page               分页对象
	 * @param lambdaQueryWrapper 查询条件
	 * @return 空间管理分页列表
	 */
	Page<Space> getSpacePageListAsManage(Page<Space> page, LambdaQueryWrapper<Space> lambdaQueryWrapper);

	// endregion 查询相关
}
