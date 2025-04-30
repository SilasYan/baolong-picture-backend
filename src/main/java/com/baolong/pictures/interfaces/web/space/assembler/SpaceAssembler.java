package com.baolong.pictures.interfaces.web.space.assembler;

import com.baolong.pictures.domain.space.aggregate.Space;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.utils.StorageUtils;
import com.baolong.pictures.interfaces.web.space.request.SpaceActivateRequest;
import com.baolong.pictures.interfaces.web.space.request.SpaceAddRequest;
import com.baolong.pictures.interfaces.web.space.request.SpaceEditRequest;
import com.baolong.pictures.interfaces.web.space.request.SpaceQueryRequest;
import com.baolong.pictures.interfaces.web.space.request.SpaceUpdateRequest;
import com.baolong.pictures.interfaces.web.space.response.SpaceDetailVO;
import com.baolong.pictures.interfaces.web.space.response.SpaceVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 空间转换类
 *
 * @author Baolong 2025年03月05 23:26
 * @version 1.0
 * @since 1.8
 */
public class SpaceAssembler {

	/**
	 * 空间激活请求 转为 空间领域对象
	 */
	public static Space toDomain(SpaceActivateRequest spaceActivateRequest) {
		Space space = new Space();
		BeanUtils.copyProperties(spaceActivateRequest, space);
		return space;
	}

	/**
	 * 空间新增请求 转为 空间领域对象
	 */
	public static Space toDomain(SpaceAddRequest spaceAddRequest) {
		Space space = new Space();
		BeanUtils.copyProperties(spaceAddRequest, space);
		return space;
	}

	/**
	 * 空间编辑请求 转为 空间领域对象
	 */
	public static Space toDomain(SpaceEditRequest spaceEditRequest) {
		Space space = new Space();
		BeanUtils.copyProperties(spaceEditRequest, space);
		return space;
	}

	/**
	 * 空间更新请求 转为 空间领域对象
	 */
	public static Space toDomain(SpaceUpdateRequest spaceUpdateRequest) {
		Space space = new Space();
		BeanUtils.copyProperties(spaceUpdateRequest, space);
		return space;
	}

	/**
	 * 空间查询请求 转为 空间领域对象
	 */
	public static Space toDomain(SpaceQueryRequest spaceQueryRequest) {
		Space space = new Space();
		BeanUtils.copyProperties(spaceQueryRequest, space);
		return space;
	}

	/**
	 * 空间领域对象 转为 空间详情响应对象
	 */
	public static SpaceDetailVO toSpaceDetailVO(Space space) {
		SpaceDetailVO spaceDetailVO = new SpaceDetailVO();
		BeanUtils.copyProperties(space, spaceDetailVO);
		spaceDetailVO.setMaxSizeUnit(StorageUtils.format(space.getMaxSize()));
		spaceDetailVO.setUsedSizeUnit(StorageUtils.format(space.getUsedSize()));
		return spaceDetailVO;
	}

	/**
	 * 空间领域对象 转为 空间详情响应对象
	 */
	public static List<SpaceDetailVO> toSpaceDetailVOList(List<Space> spaces) {
		if (spaces == null || spaces.isEmpty()) {
			return List.of();
		}
		return spaces.stream()
				.map(SpaceAssembler::toSpaceDetailVO)
				.collect(Collectors.toList());
	}

	/**
	 * 空间领域对象 转为 空间响应对象
	 */
	public static SpaceVO toSpaceVO(Space space) {
		SpaceVO spaceVO = new SpaceVO();
		BeanUtils.copyProperties(space, spaceVO);
		spaceVO.setMaxSizeUnit(StorageUtils.format(space.getMaxSize()));
		spaceVO.setUsedSizeUnit(StorageUtils.format(space.getUsedSize()));
		return spaceVO;
	}

	/**
	 * 空间领域对象分页 转为 空间响应对象分页
	 */
	public static PageVO<SpaceVO> toSpaceVOPage(PageVO<Space> spacePage) {
		return new PageVO<>(spacePage.getCurrent()
				, spacePage.getPageSize()
				, spacePage.getTotal()
				, spacePage.getPages()
				, Optional.ofNullable(spacePage.getRecords())
				.orElse(List.of()).stream()
				.map(SpaceAssembler::toSpaceVO)
				.collect(Collectors.toList())
		);
	}
}
