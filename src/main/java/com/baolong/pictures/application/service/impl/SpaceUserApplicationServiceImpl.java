package com.baolong.pictures.application.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baolong.pictures.application.service.SpaceUserApplicationService;
import com.baolong.pictures.application.service.UserApplicationService;
import com.baolong.pictures.domain.space.entity.SpaceUser;
import com.baolong.pictures.domain.space.enums.SpaceRoleEnum;
import com.baolong.pictures.domain.space.service.SpaceUserDomainService;
import com.baolong.pictures.domain.user.entity.User;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.interfaces.assembler.SpaceUserAssembler;
import com.baolong.pictures.interfaces.assembler.UserAssembler;
import com.baolong.pictures.interfaces.dto.space.SpaceUserAddRequest;
import com.baolong.pictures.interfaces.dto.space.SpaceUserEditRequest;
import com.baolong.pictures.interfaces.dto.space.SpaceUserQueryRequest;
import com.baolong.pictures.interfaces.vo.space.SpaceUserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 空间用户应用服务实现
 */
@Service
@RequiredArgsConstructor
public class SpaceUserApplicationServiceImpl implements SpaceUserApplicationService {

	private final SpaceUserDomainService spaceUserDomainService;
	private final UserApplicationService userApplicationService;

	// region 增删改相关

	/**
	 * 新增空间用户到空间
	 *
	 * @param spaceUserAddRequest 空间用户新增请求
	 * @return 是否成功
	 */
	@Override
	public Boolean addSpaceUserToSpace(SpaceUserAddRequest spaceUserAddRequest) {
		// 校验是否已经加入该空间
		SpaceUser spaceUser = spaceUserDomainService.getSpaceUserBySpaceIdAndUserId(spaceUserAddRequest.getSpaceId(), spaceUserAddRequest.getUserId());
		ThrowUtils.throwIf(spaceUser != null, ErrorCode.OPERATION_ERROR, "当前用户已在该空间");
		// 校验当前用户是否有权限
		User loginUser = userApplicationService.getLoginUser();
		this.checkSpaceUserAuth(spaceUserAddRequest.getSpaceId(), loginUser);
		Boolean flag = spaceUserDomainService.addSpaceUserToSpace(SpaceUserAssembler.toSpaceUserEntity(spaceUserAddRequest));
		ThrowUtils.throwIf(!flag, ErrorCode.OPERATION_ERROR, "空间用户新增失败");
		return true;
	}

	/**
	 * 编辑空间用户权限
	 *
	 * @param spaceUserEditRequest 空间用户编辑请求
	 * @return 是否成功
	 */
	@Override
	public Boolean editSpaceUserAuth(SpaceUserEditRequest spaceUserEditRequest) {
		// 校验当前用户是否有权限
		User loginUser = userApplicationService.getLoginUser();
		this.checkSpaceUserAuth(spaceUserEditRequest.getSpaceId(), loginUser);
		Boolean flag = spaceUserDomainService.editSpaceUserAuth(SpaceUserAssembler.toSpaceUserEntity(spaceUserEditRequest));
		ThrowUtils.throwIf(!flag, ErrorCode.OPERATION_ERROR, "空间用户权限修改失败");
		return true;
	}

	/**
	 * 删除空间用户
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	@Override
	public Boolean deleteSpaceUser(DeleteRequest deleteRequest) {
		SpaceUser spaceUser = spaceUserDomainService.getSpaceUserById(deleteRequest.getId());
		ThrowUtils.throwIf(spaceUser == null, ErrorCode.OPERATION_ERROR, "空间用户不存在");
		// 校验当前用户是否有权限
		User loginUser = userApplicationService.getLoginUser();
		this.checkSpaceUserAuth(spaceUser.getSpaceId(), loginUser);
		return spaceUserDomainService.deleteSpaceUser(deleteRequest.getId());
	}

	// endregion 增删改相关

	// region 查询相关

	/**
	 * 根据空间 ID 查询空间用户信息
	 *
	 * @param spaceUserId 空间用户 ID
	 * @return 空间用户信息
	 */
	@Override
	public SpaceUser getSpaceUserById(Long spaceUserId) {
		return spaceUserDomainService.getSpaceUserById(spaceUserId);
	}

	/**
	 * 根据空间 ID 查询空间用户分页列表
	 *
	 * @param spaceUserQueryRequest 空间用户查询请求
	 * @return 空间用户分页列表
	 */
	@Override
	public PageVO<SpaceUserVO> getSpaceUserPageListBySpaceId(SpaceUserQueryRequest spaceUserQueryRequest) {
		User loginUser = userApplicationService.getLoginUser();
		// 校验权限
		this.checkSpaceUserAuth(spaceUserQueryRequest.getSpaceId(), loginUser);
		// 查询当前用户在不在这个空间里
		Page<SpaceUser> spaceUserPage = spaceUserDomainService.getSpaceUserPageListBySpaceId(
				spaceUserQueryRequest.getPage(SpaceUser.class)
				, this.getLambdaQueryWrapper(spaceUserQueryRequest)
		);
		List<SpaceUserVO> spaceUserVO = spaceUserPage.getRecords().stream()
				.map(SpaceUserAssembler::toSpaceUserVO)
				.collect(Collectors.toList());
		// 查询用户名称
		Set<Long> userIds = spaceUserVO.stream().map(SpaceUserVO::getUserId).collect(Collectors.toSet());
		List<User> userList = userApplicationService.getUserListByIds(userIds);
		Map<Long, List<User>> userListMap = userList.stream().collect(Collectors.groupingBy(User::getId));
		spaceUserVO.forEach(spaceUser -> {
			Long userId = spaceUser.getUserId();
			User user = userListMap.get(userId).get(0);
			spaceUser.setUser(UserAssembler.toUserDetailVO(user));
		});
		return new PageVO<>(spaceUserPage.getCurrent()
				, spaceUserPage.getSize()
				, spaceUserPage.getTotal()
				, spaceUserPage.getPages()
				, spaceUserVO
		);
	}

	// endregion 查询相关

	// region 其他相关

	/**
	 * 校验空间用户权限
	 *
	 * @param spaceId   空间 ID
	 * @param loginUser 用户对象
	 */
	@Override
	public void checkSpaceUserAuth(Long spaceId, User loginUser) {
		if (loginUser.isAdmin()) return;
		SpaceUser spaceUser = spaceUserDomainService.getSpaceUserBySpaceIdAndUserId(spaceId, loginUser.getId());
		ThrowUtils.throwIf(ObjectUtil.isEmpty(spaceUser), ErrorCode.NO_AUTH_ERROR, "用户无权限");
		ThrowUtils.throwIf(!SpaceRoleEnum.CREATOR.getKey().equals(spaceUser.getSpaceRole()), ErrorCode.NO_AUTH_ERROR, "用户无权限");
	}

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param spaceUserQueryRequest 空间用户查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@Override
	public LambdaQueryWrapper<SpaceUser> getLambdaQueryWrapper(SpaceUserQueryRequest spaceUserQueryRequest) {
		ThrowUtils.throwIf(spaceUserQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
		return spaceUserDomainService.getLambdaQueryWrapper(spaceUserQueryRequest);
	}

	// endregion 其他相关
}




