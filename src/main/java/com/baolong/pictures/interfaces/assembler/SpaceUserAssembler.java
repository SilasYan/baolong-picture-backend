package com.baolong.pictures.interfaces.assembler;

import com.baolong.pictures.domain.space.entity.SpaceUser;
import com.baolong.pictures.interfaces.dto.space.SpaceUserAddRequest;
import com.baolong.pictures.interfaces.dto.space.SpaceUserEditRequest;
import com.baolong.pictures.interfaces.vo.space.SpaceUserVO;
import org.springframework.beans.BeanUtils;

/**
 * 空间用户转换类
 *
 * @author Baolong 2025年03月06 20:49
 * @version 1.0
 * @since 1.8
 */
public class SpaceUserAssembler {

	/**
	 * 空间用户新增请求 转为 空间用户实体
	 */
	public static SpaceUser toSpaceUserEntity(SpaceUserAddRequest spaceUserAddRequest) {
		SpaceUser spaceUser = new SpaceUser();
		if (spaceUserAddRequest != null) {
			BeanUtils.copyProperties(spaceUserAddRequest, spaceUser);
		}
		return spaceUser;
	}

	/**
	 * 空间用户编辑请求 转为 空间用户实体
	 */
	public static SpaceUser toSpaceUserEntity(SpaceUserEditRequest spaceUserEditRequest) {
		SpaceUser spaceUser = new SpaceUser();
		if (spaceUserEditRequest != null) {
			BeanUtils.copyProperties(spaceUserEditRequest, spaceUser);
		}
		return spaceUser;
	}

	/**
	 * 空间用户实体 转为 空间用户 VO
	 */
	public static SpaceUserVO toSpaceUserVO(SpaceUser spaceUser) {
		SpaceUserVO spaceUserVO = new SpaceUserVO();
		if (spaceUser != null) {
			BeanUtils.copyProperties(spaceUser, spaceUserVO);
		}
		return spaceUserVO;
	}
}
