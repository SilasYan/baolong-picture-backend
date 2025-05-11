package com.baolong.pictures.domain.space.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.domain.space.aggregate.SpaceUser;
import com.baolong.pictures.domain.space.aggregate.enums.SpaceRoleEnum;
import com.baolong.pictures.domain.space.aggregate.enums.SpaceTypeEnum;
import com.baolong.pictures.domain.space.repository.SpaceRepository;
import com.baolong.pictures.domain.space.repository.SpaceUserRepository;
import com.baolong.pictures.infrastructure.common.exception.BusinessException;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
		if (space.getSpaceType().equals(SpaceTypeEnum.PRIVATE.getKey())) {
			// 判断是否存在个人空间
			boolean existed = spaceRepository.existedPersonSpaceByUserId(space.getUserId());
			if (existed) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已激活个人空间");
			}
			boolean result = spaceRepository.addSpace(space);
			if (!result) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "个人空间激活失败");
			}

			// 开通一个官方公共空间
			SpaceUser spaceUser = new SpaceUser();
			spaceUser.setSpaceId(100000L);
			spaceUser.setUserId(space.getUserId());
			spaceUser.setSpaceRole(SpaceRoleEnum.EDITOR.getKey());
			spaceUserRepository.addSpaceUser(spaceUser);
		} else {
			// 判断是否存在团队空间
			Space oldSpace = spaceRepository.getTeamSpaceByUserId(space.getUserId());
			if (ObjectUtil.isNotEmpty(oldSpace)) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已激活团队空间");
			}
			boolean result = spaceRepository.addSpace(space);
			if (!result) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "团队空间激活失败");
			}
			oldSpace = spaceRepository.getTeamSpaceByUserId(space.getUserId());
			SpaceUser spaceUser = new SpaceUser();
			spaceUser.setSpaceId(oldSpace.getSpaceId());
			spaceUser.setUserId(space.getUserId());
			spaceUser.setSpaceRole(SpaceRoleEnum.CREATOR.getKey());
			spaceUserRepository.addSpaceUser(spaceUser);
		}
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
	 * 判断用户是否可以还有额度
	 *
	 * @param userId  用户ID
	 * @param isAdmin 是否是管理员
	 */
	public void canCapacityInSpace(Long userId, boolean isAdmin) {
		// 管理员直接返回
		if (isAdmin) return;
		Space space = this.getPersonSpaceByUserId(userId);
		if (space.getUsedSize() >= space.getMaxSize() || space.getUsedCount() >= space.getMaxCount()) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "空间已满, 请联系管理员开通更多容量!");
		}
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

	/**
	 * 根据用户ID获取团队空间列表
	 *
	 * @param userId 用户ID
	 * @return 团队空间列表
	 */
	public List<Space> getTeamSpaceListByUserId(Long userId) {
		List<SpaceUser> spaceUserList = spaceUserRepository.getTeamSpaceListByUserId(userId);
		if (CollUtil.isNotEmpty(spaceUserList)) {
			Set<Long> spaceIds = spaceUserList.stream().map(SpaceUser::getSpaceId).collect(Collectors.toSet());
			return spaceRepository.getSpaceListBySpaceIdList(spaceIds);
		}
		return List.of();
	}

	/**
	 * 根据空间ID获取空间详情
	 *
	 * @param spaceId 空间ID
	 * @return 空间详情
	 */
	public Space getSpaceDetailBySpaceId(Long spaceId) {
		Space space = this.getSpaceBySpaceId(spaceId);
		// 获取当前登录用户在当前空间的权限
		SpaceUser spaceUser = spaceUserRepository.getSpaceUserBySpaceIdAndUserId(spaceId, StpUtil.getLoginIdAsLong());
		if (spaceUser == null) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "暂无空间访问权限!");
		}
		space.setSpaceRole(spaceUser.getSpaceRole());
		return space;
	}

	/**
	 * 根据用户ID获取创建的团队空间
	 *
	 * @param userId 登录用户ID
	 * @return 空间领域对象
	 */
	public Space getTeamSpaceByUserId(Long userId) {
		Space space = spaceRepository.getTeamSpaceByUserId(userId);
		if (space == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "请先激活团队空间");
		}
		return space;
	}

	/**
	 * 获取空间用户列表
	 *
	 * @param spaceUser 空间用户领域对象
	 * @return 空间用户列表
	 */
	public List<SpaceUser> getSpaceUserList(SpaceUser spaceUser) {
		return spaceUserRepository.getSpaceUserList(spaceUser);
	}

	/**
	 * 新增用户到空间
	 *
	 * @param spaceUser 空间用户领域对象
	 */
	public void addSpaceUser(SpaceUser spaceUser) {
		Long spaceId = spaceUser.getSpaceId();
		Space space = spaceRepository.getSpaceBySpaceId(spaceId);
		if (space == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "空间不存在");
		}
		long userId = StpUtil.getLoginIdAsLong();
		if (!space.getUserId().equals(userId)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有操作权限");
		}
		SpaceUser oldSpaceUser = spaceUserRepository.getSpaceUserBySpaceIdAndUserId(spaceUser.getSpaceId(), spaceUser.getUserId());
		if (oldSpaceUser != null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已加入该空间");
		}
		boolean result = spaceUserRepository.addSpaceUser(spaceUser);
		if (!result) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户加入空间失败");
		}
	}

	/**
	 * 修改用户在空间的权限
	 *
	 * @param spaceUser 空间用户领域对象
	 */
	public void updateSpaceUserRole(SpaceUser spaceUser) {
		Long spaceId = spaceUser.getSpaceId();
		Space space = spaceRepository.getSpaceBySpaceId(spaceId);
		if (space == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "空间不存在");
		}
		long userId = StpUtil.getLoginIdAsLong();
		if (!space.getUserId().equals(userId)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有操作权限");
		}
		SpaceUser oldSpaceUser = spaceUserRepository.getSpaceUserBySpaceIdAndUserId(spaceUser.getSpaceId(), spaceUser.getUserId());
		if (oldSpaceUser == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "用户未加入该空间");
		}
		boolean result = spaceUserRepository.updateSpaceUserRole(spaceUser);
		if (!result) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间用户角色修改失败");
		}
	}

	/**
	 * 删除空间用户
	 *
	 * @param spaceUserId 空间用户ID
	 */
	public void deleteSpaceUser(Long spaceUserId) {
		boolean result = spaceUserRepository.deleteSpaceUser(spaceUserId);
		if (!result) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间用户删除失败");
		}
	}
}




