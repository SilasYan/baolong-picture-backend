package com.baolong.pictures.infrastructure.persistence.space.spaceUser.repository;

import com.baolong.pictures.domain.space.aggregate.SpaceUser;
import com.baolong.pictures.domain.space.repository.SpaceUserRepository;
import com.baolong.pictures.infrastructure.persistence.space.spaceUser.converter.SpaceUserConverter;
import com.baolong.pictures.infrastructure.persistence.space.spaceUser.mybatis.SpaceUserDO;
import com.baolong.pictures.infrastructure.persistence.space.spaceUser.mybatis.SpaceUserPersistenceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 空间用户表 (space_user) - 仓储服务实现
 *
 * @author Baolong 2025年03月21 09:43
 * @version 1.0
 * @since 1.8
 */
@Repository
@RequiredArgsConstructor
public class SpaceUserRepositoryImpl implements SpaceUserRepository {

	private final SpaceUserPersistenceService spaceUserPersistenceService;

	/**
	 * 根据空间ID和用户ID查询空间用户
	 *
	 * @param spaceId 空间 ID
	 * @param userId  用户 ID
	 * @return 空间用户领域对象
	 */
	@Override
	public SpaceUser getSpaceUserBySpaceIdAndUserId(Long spaceId, Long userId) {
		SpaceUserDO spaceUserDO = spaceUserPersistenceService.getOne(new LambdaQueryWrapper<SpaceUserDO>()
				.eq(SpaceUserDO::getSpaceId, spaceId)
				.eq(SpaceUserDO::getUserId, userId)
		);
		if (spaceUserDO == null) {
			return null;
		}
		return SpaceUserConverter.toDomain(spaceUserDO);
	}

	/**
	 * 根据用户ID获取团队空间列表
	 *
	 * @param userId 用户ID
	 * @return 团队空间列表
	 */
	@Override
	public List<SpaceUser> getTeamSpaceListByUserId(Long userId) {
		List<SpaceUserDO> spaceUserDOList = spaceUserPersistenceService.list(new LambdaQueryWrapper<SpaceUserDO>()
				.eq(SpaceUserDO::getUserId, userId)
		);
		return SpaceUserConverter.toDomainList(spaceUserDOList);
	}

	/**
	 * 添加空间用户
	 *
	 * @param spaceUser 空间用户领域对象
	 * @return 是否成功
	 */
	@Override
	public boolean addSpaceUser(SpaceUser spaceUser) {
		SpaceUserDO spaceUserDO = SpaceUserConverter.toDO(spaceUser);
		return spaceUserPersistenceService.save(spaceUserDO);
	}
}
