package com.baolong.pictures.interfaces.web.space.assembler;

import com.baolong.pictures.domain.space.aggregate.SpaceUser;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.space.request.SpaceUserAddRequest;
import com.baolong.pictures.interfaces.web.space.request.SpaceUserEditRequest;
import com.baolong.pictures.interfaces.web.space.request.SpaceUserQueryRequest;
import com.baolong.pictures.interfaces.web.space.response.SpaceUserVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 空间用户转换类
 *
 * @author Baolong 2025年03月06 20:49
 * @version 1.0
 * @since 1.8
 */
public class SpaceUserAssembler {

	/**
	 * 空间用户新增请求 转为 空间用户领域对象
	 */
	public static SpaceUser toDomain(SpaceUserAddRequest spaceUserAddRequest) {
		SpaceUser spaceUser = new SpaceUser();
		BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
		return spaceUser;
	}

	/**
	 * 空间用户编辑请求 转为 空间用户领域对象
	 */
	public static SpaceUser toDomain(SpaceUserEditRequest spaceUserEditRequest) {
		SpaceUser spaceUser = new SpaceUser();
		BeanUtils.copyProperties(spaceUserEditRequest, spaceUser);
		return spaceUser;
	}

	/**
	 * 空间用户查询请求 转为 空间用户领域对象
	 */
	public static SpaceUser toDomain(SpaceUserQueryRequest spaceUserQueryRequest) {
		SpaceUser spaceUser = new SpaceUser();
		BeanUtils.copyProperties(spaceUserQueryRequest, spaceUser);
		return spaceUser;
	}

	/**
	 * 空间用户领域对象 转为 空间用户响应对象
	 */
	public static SpaceUserVO toSpaceUserVO(SpaceUser spaceUser) {
		SpaceUserVO spaceUserVO = new SpaceUserVO();
		BeanUtils.copyProperties(spaceUser, spaceUserVO);
		return spaceUserVO;
	}

	/**
	 * 空间用户领域对象分页 转为 空间用户响应对象分页
	 */
	public static PageVO<SpaceUserVO> toSpaceUserVOPageVO(PageVO<SpaceUser> spaceUserPageVO) {
		return new PageVO<>(spaceUserPageVO.getCurrent()
				, spaceUserPageVO.getPageSize()
				, spaceUserPageVO.getTotal()
				, spaceUserPageVO.getPages()
				, Optional.ofNullable(spaceUserPageVO.getRecords())
				.orElse(List.of())
				.stream()
				.map(SpaceUserAssembler::toSpaceUserVO)
				.collect(Collectors.toList())
		);
	}
}
