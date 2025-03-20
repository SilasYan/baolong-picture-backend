package com.baolong.pictures.interfaces.web.user.assembler;

import cn.dev33.satoken.stp.StpUtil;
import com.baolong.pictures.domain.user.aggregate.User;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.user.request.UserAddRequest;
import com.baolong.pictures.interfaces.web.user.request.UserEditRequest;
import com.baolong.pictures.interfaces.web.user.request.UserQueryRequest;
import com.baolong.pictures.interfaces.web.user.request.UserUpdateRequest;
import com.baolong.pictures.interfaces.web.user.response.LoginUserVO;
import com.baolong.pictures.interfaces.web.user.response.UserDetailVO;
import com.baolong.pictures.interfaces.web.user.response.UserVO;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 用户转换类
 *
 * @author Baolong 2025年03月04 23:37
 * @version 1.0
 * @since 1.8
 */
public class UserAssembler {

	/**
	 * 用户添加请求 转为 用户领域模型
	 */
	public static User toUserEntity(UserAddRequest userAddRequest) {
		User user = new User();
		if (userAddRequest != null) {
			BeanUtils.copyProperties(userAddRequest, user);
		}
		return user;
	}

	/**
	 * 用户更新请求 转为 用户领域模型
	 */
	public static User toUserEntity(UserUpdateRequest userUpdateRequest) {
		User user = new User();
		if (userUpdateRequest != null) {
			BeanUtils.copyProperties(userUpdateRequest, user);
		}
		return user;
	}

	/**
	 * 用户编辑请求 转为 用户领域模型
	 */
	public static User toUserEntity(UserEditRequest userEditRequest) {
		User user = new User();
		if (userEditRequest != null) {
			BeanUtils.copyProperties(userEditRequest, user);
		}
		return user;
	}

	/**
	 * 用户查询请求 转为 用户领域模型
	 */
	public static User toUserEntity(UserQueryRequest userQueryRequest) {
		User user = new User();
		if (userQueryRequest != null) {
			BeanUtils.copyProperties(userQueryRequest, user);
		}
		return user;
	}

	/**
	 * 用户领域模型 转为 登录用户响应
	 */
	public static LoginUserVO toLoginUserVO(User user) {
		LoginUserVO loginUserVO = new LoginUserVO();
		if (user != null) {
			BeanUtils.copyProperties(user, loginUserVO);
			loginUserVO.setToken(StpUtil.getTokenInfo().getTokenValue());
		}
		return loginUserVO;
	}

	/**
	 * 用户领域模型 转为 用户详情响应
	 */
	public static UserDetailVO toUserDetailVO(User user) {
		UserDetailVO userDetailVO = new UserDetailVO();
		if (user != null) {
			BeanUtils.copyProperties(user, userDetailVO);
		}
		return userDetailVO;
	}

	/**
	 * 用户领域模型 转为 用户响应
	 */
	public static UserVO toUserVO(User user) {
		UserVO userVO = new UserVO();
		if (user != null) {
			BeanUtils.copyProperties(user, userVO);
		}
		return userVO;
	}

	/**
	 * 用户实体分页 转为 用户响应 分页
	 */
	public static PageVO<UserVO> toUserVOPage(PageVO<User> userPage) {
		List<UserVO> userVOList = Optional.ofNullable(userPage.getRecords())
				.orElse(List.of()).stream()
				.map(UserAssembler::toUserVO)
				.collect(Collectors.toList());
		return new PageVO<>(userPage.getCurrent()
				, userPage.getPageSize()
				, userPage.getTotal()
				, userPage.getPages()
				, userVOList
		);
	}
}
