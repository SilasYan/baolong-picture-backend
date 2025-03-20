package com.baolong.pictures.application.shared.satoken;

import cn.dev33.satoken.stp.StpInterface;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限接口
 *
 * @author Baolong 2025年03月10 23:41
 * @version 1.0
 * @since 1.8
 */
@Component
public class SaTokenInterface implements StpInterface {

	/**
	 * 返回一个账号所拥有的权限码集合
	 */
	@Override
	public List<String> getPermissionList(Object loginId, String loginType) {
		return List.of();
	}

	/**
	 * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
	 */
	@Override
	public List<String> getRoleList(Object loginId, String loginType) {
		return List.of();
	}
}
