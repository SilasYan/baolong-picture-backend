package com.baolong.pictures.application.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baolong.pictures.application.service.SpaceApplicationService;
import com.baolong.pictures.domain.space.entity.Space;
import com.baolong.pictures.domain.space.entity.SpaceUser;
import com.baolong.pictures.domain.space.enums.SpaceLevelEnum;
import com.baolong.pictures.domain.space.enums.SpaceRoleEnum;
import com.baolong.pictures.domain.space.enums.SpaceTypeEnum;
import com.baolong.pictures.domain.space.service.SpaceDomainService;
import com.baolong.pictures.domain.space.service.SpaceUserDomainService;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baolong.pictures.infrastructure.exception.ThrowUtils;
import com.baolong.pictures.interfaces.web.assembler.SpaceAssembler;
import com.baolong.pictures.interfaces.web.request.space.SpaceActivateRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceAddRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceEditRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceQueryRequest;
import com.baolong.pictures.interfaces.web.request.space.SpaceUpdateRequest;
import com.baolong.pictures.interfaces.web.response.space.SpaceDetailVO;
import com.baolong.pictures.interfaces.web.response.space.SpaceLevelVO;
import com.baolong.pictures.interfaces.web.response.space.SpaceVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 空间应用服务实现
 */
@Service
@RequiredArgsConstructor
public class SpaceApplicationServiceImpl implements SpaceApplicationService {

	private final SpaceDomainService spaceDomainService;
	private final SpaceUserDomainService spaceUserDomainService;
	private final UserApplicationService userApplicationService;

	@Resource
	private TransactionTemplate transactionTemplate;

	// region 其他相关

	/**
	 * 校验空间上传权限
	 *
	 * @param spaceId   空间 ID
	 * @param loginUser 用户对象
	 */
	@Override
	public void checkSpaceUploadAuth(Long spaceId, User loginUser) {
		if (loginUser.isAdmin()) return;
		if (ObjectUtil.isNotNull(spaceId) && !spaceId.equals(0L)) {
			// 空间图库: 私有空间/团队空间
			Space space = this.getSpaceInfoById(spaceId);
			ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
			// 校验空间大小和总数
			space.validSpaceSizeAndCount();
			// 判断是 私有空间 还是 团队空间
			if (SpaceTypeEnum.PRIVATE.getKey() == space.getSpaceType()) {
				// 私有空间, 必须本人才能上传
				ThrowUtils.throwIf(!space.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR, "没有空间访问权限");
			} else {
				// 团队空间, 必须空间创建人/编辑者才能上传
				SpaceUser spaceUser = spaceUserDomainService.getSpaceUserBySpaceIdAndUserId(spaceId, loginUser.getId());
				ThrowUtils.throwIf(spaceUser == null, ErrorCode.NO_AUTH_ERROR, "没有空间访问权限");
				String spaceRole = spaceUser.getSpaceRole();
				if (!SpaceRoleEnum.CREATOR.getKey().equals(spaceRole) && !SpaceRoleEnum.EDITOR.getKey().equals(spaceRole)) {
					throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "没有空间访问权限");
				}
			}
		}
	}

	/**
	 * 校验空间新增权限
	 *
	 * @param space     空间对象
	 * @param user      用户对象
	 * @param loginUser 登录用户对象
	 */
	@Override
	public void checkSpaceAddAuth(Space space, User user, User loginUser) {
		// 查询当前用户是否已经有 私人空间
		if (SpaceLevelEnum.COMMON.getKey() == space.getSpaceLevel()) {
			Boolean existed = spaceDomainService.existSpaceByUserIdAndSpaceType(user.getId(), SpaceTypeEnum.PRIVATE.getKey());
			ThrowUtils.throwIf(existed, ErrorCode.OPERATION_ERROR, "用户已开通个人空间");
		}
		// 只有管理员可以创建团队空间
		if (SpaceTypeEnum.TEAM.getKey() == space.getSpaceType() && !loginUser.isAdmin()) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建团队空间");
		}
		// 权限校验
		if (SpaceLevelEnum.COMMON.getKey() != space.getSpaceLevel() && !loginUser.isAdmin()) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建当前级别的空间");
		}
	}

	/**
	 * 校验空间编辑权限
	 *
	 * @param space     空间对象
	 * @param loginUser 登录用户对象
	 */
	@Override
	public void checkSpaceChangeAuth(Space space, User loginUser) {
		if (Objects.equals(space.getUserId(), loginUser.getId()) || loginUser.isAdmin()) return;
		// 只有管理员可以创建团队空间
		if (SpaceTypeEnum.TEAM.getKey() == space.getSpaceType() && !loginUser.isAdmin()) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作");
		}
		// 权限校验
		if (SpaceLevelEnum.COMMON.getKey() != space.getSpaceLevel() && !loginUser.isAdmin()) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限操作该级别的空间");
		}
	}

	/**
	 * 校验空间是否存在
	 *
	 * @param spaceId 空间 ID
	 */
	@Override
	public void checkSpaceExisted(Long spaceId) {
		Boolean existed = spaceDomainService.existSpaceById(spaceId);
		ThrowUtils.throwIf(!existed, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
	}

	/**
	 * 获取查询条件对象（Lambda）
	 *
	 * @param spaceQueryRequest 空间查询请求
	 * @return 查询条件对象（Lambda）
	 */
	@Override
	public LambdaQueryWrapper<Space> getLambdaQueryWrapper(SpaceQueryRequest spaceQueryRequest) {
		ThrowUtils.throwIf(spaceQueryRequest == null, ErrorCode.PARAMS_ERROR, "请求参数为空");
		return spaceDomainService.getLambdaQueryWrapper(spaceQueryRequest);
	}

	// endregion 其他相关

	// region 增删改

	/**
	 * 激活空间
	 *
	 * @param spaceActivateRequest 空间激活请求
	 * @return 空间 ID
	 */
	@Override
	public Long activateSpace(SpaceActivateRequest spaceActivateRequest) {
		Space space = SpaceAssembler.toSpaceEntity(spaceActivateRequest);
		// 填充默认值
		space.fillSpaceDefaultValue();
		// 校验参数
		space.validSpaceActivateAndAddRequest();
		User loginUser = userApplicationService.getLoginUserDetail();
		Long userId = loginUser.getId();
		// 校验空间和权限
		this.checkSpaceAddAuth(space, loginUser, loginUser);
		space.setUserId(userId);
		Boolean flag = spaceDomainService.addSpace(space);
		ThrowUtils.throwIf(!flag, ErrorCode.OPERATION_ERROR, "空间激活失败");
		return space.getId();
	}

	/**
	 * 新增空间
	 *
	 * @param spaceAddRequest 空间新增请求
	 * @return 空间 ID
	 */
	@Override
	public Long addSpace(SpaceAddRequest spaceAddRequest) {
		// 查询要给那个用户新增空间
		User changeUser = userApplicationService.getUserDetailById(spaceAddRequest.getUserId());
		ThrowUtils.throwIf(changeUser == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		Long changeUserId = changeUser.getId();
		Space space = SpaceAssembler.toSpaceEntity(spaceAddRequest);
		// 填充默认值
		space.fillSpaceDefaultValue();
		// 校验参数
		space.validSpaceActivateAndAddRequest();
		User loginUser = userApplicationService.getLoginUserDetail();
		// 校验空间和权限
		this.checkSpaceAddAuth(space, changeUser, loginUser);
		space.setUserId(changeUserId);
		// 新增空间, 加锁
		this.addSpaceByLock(space);
		return space.getId();
	}

	/**
	 * 新增空间, 加锁
	 *
	 * @param space 空间对象
	 */
	private void addSpaceByLock(Space space) {
		Map<Long, Object> lockMap = new ConcurrentHashMap<>();
		Object lock = lockMap.computeIfAbsent(space.getUserId(), key -> new Object());
		synchronized (lock) {
			try {
				transactionTemplate.execute(status -> {
					Boolean flag = spaceDomainService.addSpace(space);
					ThrowUtils.throwIf(!flag, ErrorCode.OPERATION_ERROR, "空间新增失败");
					// 如果是团队空间，关联新增团队成员记录
					if (SpaceTypeEnum.TEAM.getKey() == space.getSpaceType()) {
						SpaceUser spaceUser = new SpaceUser();
						spaceUser.setSpaceId(space.getId());
						spaceUser.setUserId(space.getUserId());
						spaceUser.setSpaceRole(SpaceRoleEnum.CREATOR.getKey());
						Boolean result = spaceUserDomainService.addSpaceUserToSpace(spaceUser);
						ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "团队成员新增失败");
					}
					return space.getId();
				});
			} finally {
				lockMap.remove(space.getUserId());
			}
		}
	}

	/**
	 * 删除空间
	 *
	 * @param deleteRequest 删除请求
	 * @return 是否成功
	 */
	@Override
	public Boolean deleteSpace(DeleteRequest deleteRequest) {
		Space space = spaceDomainService.getSpaceById(deleteRequest.getId());
		ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
		User loginUser = userApplicationService.getLoginUserDetail();
		this.checkSpaceChangeAuth(space, loginUser);
		return spaceDomainService.deleteSpace(deleteRequest.getId());
	}

	/**
	 * 更新空间
	 *
	 * @param spaceUpdateRequest 空间更新请求
	 * @return 是否成功
	 */
	@Override
	public Boolean updateSpace(SpaceUpdateRequest spaceUpdateRequest) {
		this.checkSpaceExisted(spaceUpdateRequest.getId());
		Space space = SpaceAssembler.toSpaceEntity(spaceUpdateRequest);
		User loginUser = userApplicationService.getLoginUserDetail();
		this.checkSpaceChangeAuth(space, loginUser);
		return spaceDomainService.updateSpace(space);
	}

	/**
	 * 编辑空间
	 *
	 * @param spaceEditRequest 空间编辑请求
	 * @return 是否成功
	 */
	@Override
	public Boolean editSpace(SpaceEditRequest spaceEditRequest) {
		this.checkSpaceExisted(spaceEditRequest.getId());
		Space space = SpaceAssembler.toSpaceEntity(spaceEditRequest);
		User loginUser = userApplicationService.getLoginUserDetail();
		this.checkSpaceChangeAuth(space, loginUser);
		return spaceDomainService.updateSpace(space);
	}

	/**
	 * 更新空间大小和数量
	 *
	 * @param spaceId  空间 ID
	 * @param picSize  图片大小
	 * @param picCount 图片数量
	 * @return 是否成功
	 */
	@Override
	public Boolean updateSpaceSizeAndCount(Long spaceId, Long picSize, Long picCount) {
		LambdaUpdateWrapper<Space> updateWrapper = new LambdaUpdateWrapper<>();
		updateWrapper.eq(Space::getId, spaceId);
		updateWrapper.setSql("used_size = used_size + (" + picSize + ")");
		updateWrapper.setSql("used_count = used_count + (" + picCount + ")");
		return spaceDomainService.updateSpaceSizeAndCount(updateWrapper);
	}

	// endregion 增删改

	// region 查询相关

	/**
	 * 获取登录用户的空间详情
	 *
	 * @return 登录用户的空间详情
	 */
	@Override
	public SpaceDetailVO getSpaceDetailByLoginUser() {
		User loginUser = userApplicationService.getLoginUserDetail();
		Space space = this.getSpaceInfoByUserId(loginUser.getId());
		return SpaceAssembler.toSpaceDetailVO(space);
	}

	/**
	 * 根据空间 ID 获取空间信息
	 *
	 * @param spaceId 空间 ID
	 * @return 空间信息
	 */
	@Override
	public Space getSpaceInfoById(Long spaceId) {
		return spaceDomainService.getSpaceById(spaceId);
	}

	/**
	 * 根据用户 ID 获取个人空间信息
	 *
	 * @param userId 用户 ID
	 * @return 空间信息
	 */
	@Override
	public Space getSpaceInfoByUserId(Long userId) {
		return spaceDomainService.getOne(new LambdaQueryWrapper<Space>()
				.eq(Space::getSpaceType, SpaceTypeEnum.PRIVATE)
				.eq(Space::getUserId, userId)
		);
	}

	/**
	 * 根据空间 ID 获取空间详情
	 *
	 * @param spaceId 空间 ID
	 * @return 空间详情
	 */
	@Override
	public SpaceDetailVO getSpaceVOById(Long spaceId) {
		Space space = spaceDomainService.getSpaceById(spaceId);
		return SpaceAssembler.toSpaceDetailVO(space);
	}

	/**
	 * 获取用户空间列表
	 *
	 * @param spaceQueryRequest 空间查询请求
	 * @return 空间列表
	 */
	@Override
	public List<SpaceDetailVO> getSpaceListAsUser(SpaceQueryRequest spaceQueryRequest) {
		List<Space> spaceList = spaceDomainService.getSpaceListAsUser(getLambdaQueryWrapper(spaceQueryRequest));
		return spaceList.stream().map(SpaceAssembler::toSpaceDetailVO).collect(Collectors.toList());
	}

	/**
	 * 获取空间管理分页列表
	 *
	 * @param spaceQueryRequest 空间查询请求
	 * @return 空间管理分页列表
	 */
	@Override
	public PageVO<SpaceVO> getSpacePageListAsManage(SpaceQueryRequest spaceQueryRequest) {
		Page<Space> spacePage = spaceDomainService.getSpacePageListAsManage(
				spaceQueryRequest.getPage(Space.class), this.getLambdaQueryWrapper(spaceQueryRequest)
		);
		List<SpaceVO> spaceVOS = spacePage.getRecords().stream()
				.map(SpaceAssembler::toSpaceVO)
				.collect(Collectors.toList());
		if (CollUtil.isNotEmpty(spaceVOS)) {
			// 查询用户信息
			Set<Long> userIds = spaceVOS.stream().map(SpaceVO::getUserId).collect(Collectors.toSet());
			Map<Long, List<User>> userListMap = userApplicationService.getUserListByUserIds(userIds)
					.stream().collect(Collectors.groupingBy(User::getId));
			spaceVOS.forEach(space -> {
				Long userId = space.getUserId();
				if (userListMap.containsKey(userId)) {
					space.setUserInfo(userListMap.get(userId).get(0));
				}
			});
		}
		return new PageVO<>(spacePage.getCurrent()
				, spacePage.getSize()
				, spacePage.getTotal()
				, spacePage.getPages()
				, spaceVOS
		);
	}

	/**
	 * 获取空间等级列表
	 *
	 * @return 空间等级列表
	 */
	@Override
	public List<SpaceLevelVO> getSpaceLevelList() {
		List<SpaceLevelVO> voList = new ArrayList<>();
		for (SpaceLevelEnum levelEnum : SpaceLevelEnum.values()) {
			SpaceLevelVO vo = new SpaceLevelVO();
			vo.setKey(levelEnum.getKey());
			vo.setLabel(levelEnum.getLabel());
			vo.setMaxCount(levelEnum.getMaxCount());
			vo.setMaxSize(levelEnum.getMaxSize());
			voList.add(vo);
		}
		return voList;
	}

	// endregion 查询相关
}




