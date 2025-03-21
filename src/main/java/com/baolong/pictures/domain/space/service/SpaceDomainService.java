package com.baolong.pictures.domain.space.service;

import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.domain.space.repository.SpaceRepository;
import com.baolong.pictures.domain.space.aggregate.SpaceUser;
import com.baolong.pictures.domain.space.aggregate.enums.SpaceRoleEnum;
import com.baolong.pictures.domain.space.repository.SpaceUserRepository;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 空间表 (space) - 领域服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpaceDomainService {

	private final SpaceRepository spaceRepository;
	private final SpaceUserRepository spaceUserRepository;

	/**
	 * 激活空间
	 *
	 * @param space 空间领域对象
	 */
	public void activateSpace(Space space) {
		// 判断是否存在个人空间
		boolean existed = spaceRepository.existedPersonSpaceByUserId(space.getUserId());
		if (existed) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已激活个人空间");
		}
		boolean result = spaceRepository.addSpace(space);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "个人空间激活失败");
	}

	/**
	 * 编辑空间
	 *
	 * @param space       空间领域对象
	 * @param loginUserId 登录用户ID
	 * @param isAdmin     是否管理员
	 */
	public void editSpace(Space space, Long loginUserId, boolean isAdmin) {
		boolean existed = spaceRepository.existedSpaceBySpaceId(space.getSpaceId());
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "空间不存在");
		}
		Space oldSpace = spaceRepository.getSpaceBySpaceId(space.getSpaceId());
		// 判断当前登录用户是否是管理员, 如果不是管理员, 则判断是否是当前空间的所有者, 如果都不是, 则抛出异常
		if (!oldSpace.getUserId().equals(loginUserId) && !isAdmin) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "无权限编辑空间");
		}
		boolean result = spaceRepository.updateSpace(space);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间编辑失败");
	}

	/**
	 * 删除空间
	 *
	 * @param spaceId 空间ID
	 */
	public void deleteSpace(Long spaceId) {
		boolean existed = spaceRepository.existedSpaceBySpaceId(spaceId);
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "空间不存在");
		}
		boolean result = spaceRepository.deleteSpace(spaceId);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间删除失败");
	}

	/**
	 * 更新空间
	 *
	 * @param space 空间领域对象
	 */
	public void updateSpace(Space space) {
		boolean existed = spaceRepository.existedSpaceBySpaceId(space.getSpaceId());
		if (!existed) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "空间不存在");
		}
		boolean result = spaceRepository.updateSpace(space);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间更新失败");
	}

	/**
	 * 更新空间大小和数量
	 *
	 * @param spaceId  空间 ID
	 * @param picSize  图片大小
	 * @param picCount 图片数量
	 */
	public void updateSpaceSizeAndCount(Long spaceId, Long picSize, Long picCount) {
		boolean result = spaceRepository.updateSpaceSizeAndCount(spaceId, picSize, picCount);
		if (result) return;
		throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间额度更新失败");
	}

	/**
	 * 判断用户是否可以操作空间
	 *
	 * @param spaceId 空间ID
	 * @param userId  用户ID
	 * @param isAdmin 是否是管理员
	 */
	public void canOperateInSpace(Long spaceId, Long userId, boolean isAdmin) {
		// 管理员直接返回
		if (isAdmin) return;
		Space space = this.getSpaceBySpaceId(spaceId);
		// 如果当前空间创建人为当前用户则直接返回
		if (space.getUserId().equals(userId)) return;
		SpaceUser spaceUser = spaceUserRepository.getSpaceUserBySpaceIdAndUserId(spaceId, userId);
		String spaceRole = spaceUser.getSpaceRole();
		if (SpaceRoleEnum.isCreator(spaceRole) || SpaceRoleEnum.isEditor(spaceRole)) return;
		throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间操作权限");
	}

	/**
	 * 根据用户ID获取个人空间
	 *
	 * @param userId 用户ID
	 * @return 空间领域对象
	 */
	public Space getPersonSpaceByUserId(Long userId) {
		Space space = spaceRepository.getPersonSpaceByUserId(userId);
		if (space == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "请先激活个人空间");
		}
		return space;
	}

	/**
	 * 根据空间ID获取空间
	 *
	 * @param spaceId 空间ID
	 * @return 空间领域对象
	 */
	public Space getSpaceBySpaceId(Long spaceId) {
		Space space = spaceRepository.getSpaceBySpaceId(spaceId);
		if (space == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "空间不存在");
		}
		return space;
	}

	/**
	 * 获取空间管理分页列表
	 *
	 * @param space 空间领域模型
	 * @return 空间管理分页列表
	 */
	public PageVO<Space> getSpacePageListAsManage(Space space) {
		return spaceRepository.getSpacePageList(space);
	}
}




