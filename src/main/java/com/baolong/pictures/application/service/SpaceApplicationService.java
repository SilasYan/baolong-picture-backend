package com.baolong.pictures.application.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.domain.space.aggregate.SpaceUser;
import com.baolong.pictures.domain.space.service.SpaceDomainService;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 空间表 (space) - 应用服务
 */
@Service
@RequiredArgsConstructor
public class SpaceApplicationService {
	// 空间领域服务
	private final SpaceDomainService spaceDomainService;
	// 其他应用服务
	private final UserApplicationService userApplicationService;

	/**
	 * 激活空间
	 *
	 * @param space 空间领域对象
	 */
	public void activateSpace(Space space) {
		// 填充默认值
		space.fillSpaceDefaultValue();
		// 校验参数
		space.validSpaceActivateAndAddRequest();
		space.setUserId(StpUtil.getLoginIdAsLong());
		spaceDomainService.activateSpace(space);
	}

	/**
	 * 编辑空间
	 *
	 * @param space 空间领域对象
	 */
	public void editSpace(Space space) {
		User loginUser = userApplicationService.getLoginUserDetail();
		spaceDomainService.editSpace(space, loginUser.getUserId(), loginUser.isAdmin());
	}

	/**
	 * 删除空间
	 *
	 * @param spaceId 空间ID
	 */
	public void deleteSpace(Long spaceId) {
		spaceDomainService.deleteSpace(spaceId);
	}

	/**
	 * 更新空间
	 *
	 * @param space 空间领域模型
	 */
	public void updateSpace(Space space) {
		spaceDomainService.updateSpace(space);
	}

	/**
	 * 获取登录用户的空间详情
	 *
	 * @return 登录用户的空间详情
	 */
	public Space getSpaceDetailByLoginUser() {
		return spaceDomainService.getPersonSpaceByUserId(StpUtil.getLoginIdAsLong());
	}

	/**
	 * 获取空间管理分页列表
	 *
	 * @param space 空间领域模型
	 * @return 空间管理分页列表
	 */
	public PageVO<Space> getSpacePageListAsManage(Space space) {
		PageVO<Space> spacePage = spaceDomainService.getSpacePageListAsManage(space);
		List<Space> records = spacePage.getRecords();
		if (CollUtil.isNotEmpty(records)) {
			// 查询用户信息
			Set<Long> userIds = records.stream().map(Space::getUserId).collect(Collectors.toSet());
			Map<Long, List<User>> userListMap = userApplicationService.getUserListByUserIds(userIds)
					.stream().collect(Collectors.groupingBy(User::getUserId));
			records.forEach(s -> {
				Long userId = s.getUserId();
				if (userListMap.containsKey(userId)) {
					s.setUserInfo(userListMap.get(userId).get(0));
				}
			});
		}
		return spacePage;
	}

	/**
	 * 判断用户是否可以操作空间
	 *
	 * @param spaceId 空间ID
	 * @param userId  用户ID
	 * @param isAdmin 是否是管理员
	 */
	public void canOperateInSpace(Long spaceId, Long userId, boolean isAdmin) {
		spaceDomainService.canOperateInSpace(spaceId, userId, isAdmin);
	}

	/**
	 * 判断用户是否可以还有额度
	 *
	 * @param userId  用户ID
	 * @param isAdmin 是否是管理员
	 */
	public void canCapacityInSpace(Long userId, boolean isAdmin) {
		spaceDomainService.canCapacityInSpace(userId, isAdmin);
	}

	/**
	 * 更新空间大小和数量
	 *
	 * @param spaceId  空间 ID
	 * @param picSize  图片大小
	 * @param picCount 图片数量
	 */
	public void updateSpaceSizeAndCount(Long spaceId, Long picSize, Long picCount) {
		spaceDomainService.updateSpaceSizeAndCount(spaceId, picSize, picCount);
	}

	/**
	 * 根据空间ID获取空间
	 *
	 * @param spaceId 空间ID
	 * @return 空间领域对象
	 */
	public Space getSpaceBySpaceId(Long spaceId) {
		return spaceDomainService.getSpaceBySpaceId(spaceId);
	}

	/**
	 * 根据用户ID获取团队空间列表
	 *
	 * @param userId 用户ID
	 * @return 团队空间列表
	 */
	public List<Space> getTeamSpaceListByUserId(Long userId) {
		return spaceDomainService.getTeamSpaceListByUserId(userId);
	}

	/**
	 * 根据空间ID获取空间详情
	 *
	 * @param spaceId 空间ID
	 * @return 空间详情
	 */
	public Space getSpaceDetailBySpaceId(Long spaceId) {
		return spaceDomainService.getSpaceDetailBySpaceId(spaceId);
	}

	/**
	 * 获取登录用户的团队空间列表
	 *
	 * @return 登录用户的团队空间列表
	 */
	public List<Space> getTeamSpacesByLoginUser() {
		return this.getTeamSpaceListByUserId(StpUtil.getLoginIdAsLong());
	}

	/**
	 * 根据用户ID获取创建的团队空间
	 *
	 * @return 空间领域对象
	 */
	public Space getTeamSpaceByUserId() {
		return spaceDomainService.getTeamSpaceByUserId(StpUtil.getLoginIdAsLong());
	}

	/**
	 * 获取空间用户列表
	 *
	 * @param spaceUser 空间用户领域对象
	 * @return 空间用户列表
	 */
	public List<SpaceUser> getSpaceUserList(SpaceUser spaceUser) {
		List<SpaceUser> spaceUserList = spaceDomainService.getSpaceUserList(spaceUser);
		Set<Long> userIds = spaceUserList.stream().map(SpaceUser::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userListMap = userApplicationService.getUserListByUserIds(userIds)
				.stream().collect(Collectors.groupingBy(User::getUserId));
		for (SpaceUser su : spaceUserList) {
			Long userId = su.getUserId();
			if (userListMap.containsKey(userId)) {
				su.setUser(userListMap.get(userId).get(0));
			}
		}
		return spaceUserList;
	}

	/**
	 * 新增用户到空间
	 *
	 * @param spaceUser 空间用户领域对象
	 */
	public void addSpaceUser(SpaceUser spaceUser) {
		spaceDomainService.addSpaceUser(spaceUser);
	}

	/**
	 * 修改用户在空间的权限
	 *
	 * @param spaceUser 空间用户领域对象
	 */
	public void updateSpaceUserRole(SpaceUser spaceUser) {
		spaceDomainService.updateSpaceUserRole(spaceUser);
	}

	/**
	 * 删除空间用户
	 *
	 * @param spaceUserId 空间用户ID
	 */
	public void deleteSpaceUser(Long spaceUserId) {
		spaceDomainService.deleteSpaceUser(spaceUserId);
	}
}




