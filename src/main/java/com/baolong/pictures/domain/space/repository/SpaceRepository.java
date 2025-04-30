package com.baolong.pictures.domain.space.repository;

import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.infrastructure.common.page.PageVO;

import java.util.List;
import java.util.Set;

/**
 * 空间表 (space) - 仓储服务接口
 *
 * @author Baolong 2025年03月21 00:26
 * @version 1.0
 * @since 1.8
 */
public interface SpaceRepository {

	/**
	 * 根据用户ID判断个人空间是否存在
	 *
	 * @param userId 用户ID
	 * @return 是否存在
	 */
	boolean existedPersonSpaceByUserId(Long userId);

	/**
	 * 根据空间ID判断空间是否存在
	 *
	 * @param spaceId 空间ID
	 * @return 是否存在
	 */
	boolean existedSpaceBySpaceId(Long spaceId);

	/**
	 * 新增空间
	 *
	 * @param space 空间领域模型
	 * @return 是否成功
	 */
	boolean addSpace(Space space);

	/**
	 * 更新空间
	 *
	 * @param space 空间领域模型
	 * @return 是否成功
	 */
	boolean updateSpace(Space space);

	/**
	 * 删除空间
	 *
	 * @param spaceId 空间ID
	 * @return 是否成功
	 */
	boolean deleteSpace(Long spaceId);

	/**
	 * 更新空间大小和数量
	 *
	 * @param spaceId  空间 ID
	 * @param picSize  图片大小
	 * @param picCount 图片数量
	 * @return 是否成功
	 */
	boolean updateSpaceSizeAndCount(Long spaceId, Long picSize, Long picCount);

	/**
	 * 根据空间ID获取空间
	 *
	 * @param spaceId 空间ID
	 * @return 空间领域模型
	 */
	Space getSpaceBySpaceId(Long spaceId);

	/**
	 * 根据用户ID获取个人空间
	 *
	 * @param userId 用户ID
	 * @return 空间领域对象
	 */
	Space getPersonSpaceByUserId(Long userId);

	/**
	 * 获取空间分页列表
	 *
	 * @param space 空间领域对象
	 * @return 空间领域对象分页列表
	 */
	PageVO<Space> getSpacePageList(Space space);

	/**
	 * 根据空间ID列表获取空间列表
	 *
	 * @param spaceIds 空间列表
	 * @return 空间列表
	 */
	List<Space> getSpaceListBySpaceIdList(Set<Long> spaceIds);

	/**
	 * 根据用户ID获取创建的团队空间
	 *
	 * @param userId 登录用户ID
	 * @return 空间领域对象
	 */
	Space getTeamSpaceByUserId(Long userId);
}
