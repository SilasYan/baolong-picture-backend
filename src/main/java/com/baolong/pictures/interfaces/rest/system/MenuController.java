package com.baolong.pictures.interfaces.rest.system;

import cn.hutool.core.util.ObjectUtil;
import com.baolong.pictures.application.service.MenuApplicationService;
import com.baolong.pictures.application.shared.auth.annotation.AuthCheck;
import com.baolong.pictures.domain.system.menu.aggregate.Menu;
import com.baolong.pictures.domain.user.aggregate.constant.UserConstant;
import com.baolong.pictures.infrastructure.common.BaseResponse;
import com.baolong.pictures.infrastructure.common.DeleteRequest;
import com.baolong.pictures.infrastructure.common.ResultUtils;
import com.baolong.pictures.infrastructure.common.exception.ErrorCode;
import com.baolong.pictures.infrastructure.common.exception.ThrowUtils;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.interfaces.web.system.menu.assembler.MenuAssembler;
import com.baolong.pictures.interfaces.web.system.menu.request.MenuAddRequest;
import com.baolong.pictures.interfaces.web.system.menu.request.MenuQueryRequest;
import com.baolong.pictures.interfaces.web.system.menu.request.MenuUpdateRequest;
import com.baolong.pictures.interfaces.web.system.menu.response.MenuVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 菜单表 (menu) - 接口
 *
 * @author Silas Yan 2025-03-22:15:57
 */
@RestController
@RequestMapping("/menu")
@RequiredArgsConstructor
public class MenuController {
	private final MenuApplicationService menuApplicationService;

	/**
	 * 新增菜单
	 *
	 * @param menuAddRequest 菜单新增请求
	 * @return 新增结果
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> addMenu(@RequestBody MenuAddRequest menuAddRequest) {
		ThrowUtils.throwIf(menuAddRequest == null, ErrorCode.PARAMS_ERROR);
		Menu menu = MenuAssembler.toDomain(menuAddRequest);
		menu.checkParam();
		menuApplicationService.addMenu(menu);
		return ResultUtils.success();
	}

	/**
	 * 删除菜单
	 *
	 * @param deleteRequest 菜单删除请求
	 * @return 删除结果
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteMenu(@RequestBody DeleteRequest deleteRequest) {
		ThrowUtils.throwIf(deleteRequest == null, ErrorCode.PARAMS_ERROR);
		Long menuId = deleteRequest.getId();
		ThrowUtils.throwIf(ObjectUtil.isEmpty(menuId), ErrorCode.PARAMS_ERROR);
		menuApplicationService.deleteMenu(menuId);
		return ResultUtils.success();
	}

	/**
	 * 更新菜单
	 *
	 * @param menuUpdateRequest 菜单更新请求
	 * @return 更新结果
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateMenu(@RequestBody MenuUpdateRequest menuUpdateRequest) {
		ThrowUtils.throwIf(menuUpdateRequest == null, ErrorCode.PARAMS_ERROR);
		ThrowUtils.throwIf(ObjectUtil.isEmpty(menuUpdateRequest.getMenuId()), ErrorCode.PARAMS_ERROR);
		Menu menu = MenuAssembler.toDomain(menuUpdateRequest);
		menu.checkParam();
		menuApplicationService.updateMenu(menu);
		return ResultUtils.success();
	}

	/**
	 * 获取菜单管理分页列表
	 *
	 * @param menuQueryRequest 菜单查询请求
	 * @return 菜单管理分页列表
	 */
	@PostMapping("/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<PageVO<MenuVO>> getMenuPageList(@RequestBody MenuQueryRequest menuQueryRequest) {
		ThrowUtils.throwIf(menuQueryRequest == null, ErrorCode.PARAMS_ERROR);
		Menu menu = MenuAssembler.toDomain(menuQueryRequest);
		PageVO<Menu> menuPageVO = menuApplicationService.getMenuPageList(menu);
		return ResultUtils.success(MenuAssembler.toPageVO(menuPageVO));
	}
}
