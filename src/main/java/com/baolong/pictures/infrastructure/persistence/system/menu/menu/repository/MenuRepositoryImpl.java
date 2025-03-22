package com.baolong.pictures.infrastructure.persistence.system.menu.menu.repository;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.system.menu.aggregate.Menu;
import com.baolong.pictures.domain.system.menu.repository.MenuRepository;
import com.baolong.pictures.infrastructure.common.page.PageRequest;
import com.baolong.pictures.infrastructure.common.page.PageVO;
import com.baolong.pictures.infrastructure.persistence.system.menu.menu.converter.MenuConverter;
import com.baolong.pictures.infrastructure.persistence.system.menu.menu.mybatis.MenuDO;
import com.baolong.pictures.infrastructure.persistence.system.menu.menu.mybatis.MenuPersistenceService;
import com.baolong.pictures.infrastructure.utils.SFLambdaUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 菜单表 (menu) - 仓储服务实现
 *
 * @author Baolong 2025年03月21 22:39
 * @version 1.0
 * @since 1.8
 */
@Repository
@RequiredArgsConstructor
public class MenuRepositoryImpl implements MenuRepository {

	private final MenuPersistenceService menuPersistenceService;

	/**
	 * 查询条件对象（Lambda）
	 *
	 * @param menu 定时任务领域对象
	 * @return 查询条件对象（Lambda）
	 */
	private LambdaQueryWrapper<MenuDO> lambdaQueryWrapper(Menu menu) {
		LambdaQueryWrapper<MenuDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
		Long menuId = menu.getMenuId();
		String menuName = menu.getMenuName();
		Integer menuPosition = menu.getMenuPosition();
		String menuPath = menu.getMenuPath();
		lambdaQueryWrapper.eq(ObjUtil.isNotNull(menuId), MenuDO::getId, menuId);
		lambdaQueryWrapper.like(StrUtil.isNotEmpty(menuName), MenuDO::getMenuName, menuName);
		lambdaQueryWrapper.eq(StrUtil.isNotEmpty(menuPath), MenuDO::getMenuPath, menuPath);
		lambdaQueryWrapper.eq(ObjUtil.isNotEmpty(menuPosition), MenuDO::getMenuPosition, menuPosition);
		// 处理排序规则
		if (menu.isMultipleSort()) {
			List<PageRequest.Sort> sorts = menu.getSorts();
			if (CollUtil.isNotEmpty(sorts)) {
				sorts.forEach(sort -> {
					String sortField = sort.getField();
					boolean sortAsc = sort.isAsc();
					lambdaQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(MenuDO.class, sortField));
				});
			}
		} else {
			PageRequest.Sort sort = menu.getSort();
			if (sort != null) {
				String sortField = sort.getField();
				boolean sortAsc = sort.isAsc();
				lambdaQueryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortAsc, SFLambdaUtil.getSFunction(MenuDO.class, sortField));
			} else {
				lambdaQueryWrapper.orderByDesc(MenuDO::getCreateTime);
			}
		}
		return lambdaQueryWrapper;
	}

	/**
	 * 添加菜单
	 *
	 * @param menu 菜单领域对象
	 * @return 菜单ID
	 */
	@Override
	public Long addMenu(Menu menu) {
		MenuDO menuDO = MenuConverter.toDO(menu);
		menuPersistenceService.save(menuDO);
		return menuDO.getId();
	}

	/**
	 * 删除菜单
	 *
	 * @param menuId 菜单ID
	 * @return 是否成功
	 */
	@Override
	public boolean deleteMenu(Long menuId) {
		return menuPersistenceService.removeById(menuId);
	}

	/**
	 * 更新菜单
	 *
	 * @param menu 菜单领域对象
	 */
	@Override
	public boolean updateMenu(Menu menu) {
		return menuPersistenceService.updateById(MenuConverter.toDO(menu));
	}

	/**
	 * 根据菜单路径判断菜单是否存在
	 *
	 * @param menuPath 菜单路径
	 * @return true:存在 false:不存在
	 */
	@Override
	public boolean existedMenuByMenuPath(String menuPath) {
		return menuPersistenceService.getBaseMapper()
				.exists(new LambdaQueryWrapper<MenuDO>()
						.eq(MenuDO::getMenuPath, menuPath)
				);
	}

	/**
	 * 根据菜单ID判断菜单是否存在
	 *
	 * @param menuId 菜单ID
	 * @return true:存在 false:不存在
	 */
	@Override
	public boolean existedMenuByMenuId(Long menuId) {
		return menuPersistenceService.getBaseMapper()
				.exists(new LambdaQueryWrapper<MenuDO>()
						.eq(MenuDO::getId, menuId)
				);
	}

	/**
	 * 根据菜单ID列表获取菜单列表
	 *
	 * @param menuIdList 菜单ID列表
	 * @return 菜单列表
	 */
	@Override
	public List<Menu> getMenuListByMenuIdList(Set<Long> menuIdList) {
		List<MenuDO> menuDOList = menuPersistenceService.listByIds(menuIdList);
		return MenuConverter.toDomainList(menuDOList);
	}

	/**
	 * 根据菜单路径获取菜单
	 *
	 * @param menuPath 菜单路径
	 * @return 菜单
	 */
	@Override
	public Menu getMenuByMenuPath(String menuPath) {
		MenuDO menuDO = menuPersistenceService.getOne(new LambdaQueryWrapper<MenuDO>()
				.eq(MenuDO::getMenuPath, menuPath)
		);
		return MenuConverter.toDomain(menuDO);
	}

	/**
	 * 获取菜单管理分页列表
	 *
	 * @param menu 菜单领域对象
	 * @return 菜单管理分页列表
	 */
	@Override
	public PageVO<Menu> getMenuPageList(Menu menu) {
		LambdaQueryWrapper<MenuDO> lambdaQueryWrapper = this.lambdaQueryWrapper(menu);
		Page<MenuDO> page = menuPersistenceService.page(menu.getPage(MenuDO.class), lambdaQueryWrapper);
		return MenuConverter.toDomainPage(page);
	}
}
