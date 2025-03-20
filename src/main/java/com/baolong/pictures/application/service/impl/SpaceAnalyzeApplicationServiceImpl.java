// package com.baolong.pictures.application.service.impl;
//
// import cn.hutool.core.util.NumberUtil;
// import cn.hutool.core.util.ObjUtil;
// import com.baolong.pictures.domain.picture.entity.TePicture;
// import com.baolong.pictures.domain.space.entity.TeSpace;
// import com.baolong.pictures.domain.user.entity.TUser;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceCategoryAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceRankAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceSizeAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceTagAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceUsageAnalyzeRequest;
// import com.baolong.pictures.interfaces.dto.space.analyze.SpaceUserAnalyzeRequest;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceCategoryAnalyzeResponse;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceSizeAnalyzeResponse;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceTagAnalyzeResponse;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceUsageAnalyzeResponse;
// import com.baolong.pictures.interfaces.vo.space.analyze.SpaceUserAnalyzeResponse;
// import com.baolong.pictures.application.service.SpaceAnalyzeApplicationService;
// import com.baolong.pictures.application.service.SpaceApplicationService;
// import com.baolong.pictures.application.service.CategoryTagApplicationService;
// import com.baolong.pictures.application.service.PictureApplicationService;
// import com.baolong.pictures.application.service.UserApplicationService;
// import com.baolong.pictures.domain.categoryTag.entity.CategoryTag;
// import com.baolong.pictures.domain.categoryTag.service.CategoryTagDomainService;
// import com.baolong.pictures.infrastructure.exception.BusinessException;
// import com.baolong.pictures.infrastructure.exception.ErrorCode;
// import com.baolong.pictures.infrastructure.exception.ThrowUtils;
// import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
// import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.stereotype.Service;
//
// import javax.annotation.Resource;
// import java.util.Arrays;
// import java.util.LinkedHashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Objects;
// import java.util.stream.Collectors;
//
// /**
//  * 空间分析服务实现
//  *
//  * @author Baolong 2025年02月27 19:18
//  * @version 1.0
//  * @since 1.8
//  */
// @Slf4j
// @Service
// public class SpaceAnalyzeApplicationServiceImpl implements SpaceAnalyzeApplicationService {
// 	@Resource
// 	private UserApplicationService userApplicationService;
// 	@Resource
// 	private SpaceApplicationService spaceApplicationService;
// 	@Resource
// 	private PictureApplicationService pictureApplicationService;
// 	@Resource
// 	private CategoryTagApplicationService categoryTagApplicationService;
// 	@Resource
// 	private CategoryTagDomainService categoryTagDomainService;
//
// 	/**
// 	 * 获取空间使用分析数据
// 	 *
// 	 * @param spaceUsageAnalyzeRequest 空间资源分析请求
// 	 * @param loginTUser               当前登录用户
// 	 * @return SpaceUsageAnalyzeResponse 分析结果
// 	 */
// 	@Override
// 	public SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, TUser loginTUser) {
// 		ThrowUtils.throwIf(spaceUsageAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
// 		if (spaceUsageAnalyzeRequest.isQueryAll() || spaceUsageAnalyzeRequest.isQueryPublic()) {
// 			// 查询全部或公共图库逻辑
// 			// 仅管理员可以访问
// 			boolean isAdmin = loginTUser.isAdmin();
// 			ThrowUtils.throwIf(!isAdmin, ErrorCode.NO_AUTH_ERROR, "无权访问空间");
// 			// 统计公共图库的资源使用
// 			QueryWrapper<TePicture> queryWrapper = new QueryWrapper<>();
// 			queryWrapper.select("picSize");
// 			if (!spaceUsageAnalyzeRequest.isQueryAll()) {
// 				queryWrapper.isNull("spaceId");
// 			}
// 			List<Object> pictureObjList = pictureApplicationService.getPictureAppointFieldList(queryWrapper);
// 			long usedSize = pictureObjList.stream().mapToLong(result -> result instanceof Long ? (Long) result : 0).sum();
// 			long usedCount = pictureObjList.size();
// 			// 封装返回结果
// 			SpaceUsageAnalyzeResponse spaceUsageAnalyzeResponse = new SpaceUsageAnalyzeResponse();
// 			spaceUsageAnalyzeResponse.setUsedSize(usedSize);
// 			spaceUsageAnalyzeResponse.setUsedCount(usedCount);
// 			// 公共图库无上限、无比例
// 			spaceUsageAnalyzeResponse.setMaxSize(null);
// 			spaceUsageAnalyzeResponse.setSizeUsageRatio(null);
// 			spaceUsageAnalyzeResponse.setMaxCount(null);
// 			spaceUsageAnalyzeResponse.setCountUsageRatio(null);
// 			return spaceUsageAnalyzeResponse;
// 		} else {
// 			// 查询指定空间
// 			Long spaceId = spaceUsageAnalyzeRequest.getSpaceId();
// 			ThrowUtils.throwIf(spaceId == null || spaceId <= 0, ErrorCode.PARAMS_ERROR);
// 			// 获取空间信息
// 			TeSpace teSpace = spaceApplicationService.getSpaceById(spaceId);
// 			ThrowUtils.throwIf(teSpace == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
//
// 			// 权限校验：仅空间所有者或管理员可访问
// 			spaceApplicationService.checkSpaceAuth(teSpace, loginTUser);
//
// 			// 构造返回结果
// 			SpaceUsageAnalyzeResponse response = new SpaceUsageAnalyzeResponse();
// 			response.setUsedSize(teSpace.getTotalSize());
// 			response.setMaxSize(teSpace.getMaxSize());
// 			// 后端直接算好百分比，这样前端可以直接展示
// 			double sizeUsageRatio = NumberUtil.round(teSpace.getTotalSize() * 100.0 / teSpace.getMaxSize(), 2).doubleValue();
// 			response.setSizeUsageRatio(sizeUsageRatio);
// 			response.setUsedCount(teSpace.getTotalCount());
// 			response.setMaxCount(teSpace.getMaxCount());
// 			double countUsageRatio = NumberUtil.round(teSpace.getTotalCount() * 100.0 / teSpace.getMaxCount(), 2).doubleValue();
// 			response.setCountUsageRatio(countUsageRatio);
// 			return response;
// 		}
// 	}
//
// 	/**
// 	 * 获取空间分类分析数据
// 	 *
// 	 * @param spaceCategoryAnalyzeRequest 空间分类分析请求
// 	 * @param loginTUser                  当前登录用户
// 	 * @return List<SpaceCategoryAnalyzeResponse>
// 	 */
// 	@Override
// 	public List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, TUser loginTUser) {
// 		ThrowUtils.throwIf(spaceCategoryAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
//
// 		// 检查权限
// 		checkSpaceAnalyzeAuth(spaceCategoryAnalyzeRequest, loginTUser);
//
// 		// 构造查询条件
// 		QueryWrapper<TePicture> queryWrapper = new QueryWrapper<>();
// 		// 根据分析范围补充查询条件
// 		fillAnalyzeQueryWrapper(spaceCategoryAnalyzeRequest, queryWrapper);
//
// 		// 使用 MyBatis-Plus 分组查询
// 		queryWrapper.select("category AS category",
// 						"COUNT(*) AS count",
// 						"SUM(picSize) AS totalSize")
// 				.groupBy("category");
//
// 		// 查询并转换结果
// 		return pictureApplicationService.getPictureAppointFieldMaps(queryWrapper)
// 				.stream()
// 				.map(result -> {
// 					String category = result.get("category") != null ? result.get("category").toString() : "未分类";
// 					Long count = ((Number) result.get("count")).longValue();
// 					Long totalSize = ((Number) result.get("totalSize")).longValue();
// 					return new SpaceCategoryAnalyzeResponse(category, count, totalSize);
// 				})
// 				.collect(Collectors.toList());
// 	}
//
// 	/**
// 	 * 获取空间标签分析数据
// 	 *
// 	 * @param spaceTagAnalyzeRequest 空间标签分析请求
// 	 * @param loginTUser             当前登录用户
// 	 * @return List<SpaceTagAnalyzeResponse>
// 	 */
// 	@Override
// 	public List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, TUser loginTUser) {
// 		ThrowUtils.throwIf(spaceTagAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
//
// 		// 检查权限
// 		checkSpaceAnalyzeAuth(spaceTagAnalyzeRequest, loginTUser);
//
// 		// 构造查询条件
// 		QueryWrapper<TePicture> queryWrapper = new QueryWrapper<>();
// 		fillAnalyzeQueryWrapper(spaceTagAnalyzeRequest, queryWrapper);
//
// 		// 查询所有符合条件的标签
// 		queryWrapper.select("tags");
// 		List<String> tagsList = pictureApplicationService.getPictureAppointFieldList(queryWrapper)
// 				.stream()
// 				.filter(ObjUtil::isNotNull)
// 				.map(Object::toString)
// 				.collect(Collectors.toList());
//
// 		// 合并所有标签并统计使用次数
// 		Map<String, Long> tagCountMap = tagsList.stream()
// 				.flatMap(tag -> Arrays.stream(tag.split(",")))
// 				.collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
//
// 		// 查询所有标签对应的名字
// 		Map<String, String> tagMap = categoryTagDomainService.getCategoryTagList(new LambdaQueryWrapper<CategoryTag>().in(CategoryTag::getId, tagCountMap.keySet()))
// 				.stream()
// 				.filter(Objects::nonNull)
// 				.collect(Collectors.toMap(tag -> tag.getId().toString(), CategoryTag::getName));
//
// 		// 转换为响应对象，按使用次数降序排序
// 		return tagCountMap.entrySet().stream()
// 				.sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) // 降序排列
// 				.map(entry -> new SpaceTagAnalyzeResponse(tagMap.get(entry.getKey()), entry.getValue()))
// 				.collect(Collectors.toList());
// 	}
//
// 	/**
// 	 * 获取空间大小分析数据
// 	 *
// 	 * @param spaceSizeAnalyzeRequest 空间大小分析请求
// 	 * @param loginTUser              当前登录用户
// 	 * @return List<SpaceSizeAnalyzeResponse>
// 	 */
// 	@Override
// 	public List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, TUser loginTUser) {
// 		ThrowUtils.throwIf(spaceSizeAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
//
// 		// 检查权限
// 		checkSpaceAnalyzeAuth(spaceSizeAnalyzeRequest, loginTUser);
//
// 		// 构造查询条件
// 		QueryWrapper<TePicture> queryWrapper = new QueryWrapper<>();
// 		fillAnalyzeQueryWrapper(spaceSizeAnalyzeRequest, queryWrapper);
//
// 		// 查询所有符合条件的图片大小
// 		queryWrapper.select("picSize");
// 		List<Long> picSizes = pictureApplicationService.getPictureAppointFieldList(queryWrapper)
// 				.stream()
// 				.map(size -> ((Number) size).longValue())
// 				.collect(Collectors.toList());
//
// 		// 定义分段范围，注意使用有序 Map
// 		Map<String, Long> sizeRanges = new LinkedHashMap<>();
// 		sizeRanges.put("<100KB", picSizes.stream().filter(size -> size < 100 * 1024).count());
// 		sizeRanges.put("100KB-500KB", picSizes.stream().filter(size -> size >= 100 * 1024 && size < 500 * 1024).count());
// 		sizeRanges.put("500KB-1MB", picSizes.stream().filter(size -> size >= 500 * 1024 && size < 1 * 1024 * 1024).count());
// 		sizeRanges.put(">1MB", picSizes.stream().filter(size -> size >= 1 * 1024 * 1024).count());
//
// 		// 转换为响应对象
// 		return sizeRanges.entrySet().stream()
// 				.map(entry -> new SpaceSizeAnalyzeResponse(entry.getKey(), entry.getValue()))
// 				.collect(Collectors.toList());
// 	}
//
// 	/**
// 	 * 获取空间用户上传分析数据
// 	 *
// 	 * @param spaceUserAnalyzeRequest 空间用户上传分析请求
// 	 * @param loginTUser              当前登录用户
// 	 * @return List<SpaceUserAnalyzeResponse>
// 	 */
// 	@Override
// 	public List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, TUser loginTUser) {
// 		ThrowUtils.throwIf(spaceUserAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
// 		// 检查权限
// 		checkSpaceAnalyzeAuth(spaceUserAnalyzeRequest, loginTUser);
//
// 		// 构造查询条件
// 		QueryWrapper<TePicture> queryWrapper = new QueryWrapper<>();
// 		Long userId = spaceUserAnalyzeRequest.getUserId();
// 		queryWrapper.eq(ObjUtil.isNotNull(userId), "userId", userId);
// 		fillAnalyzeQueryWrapper(spaceUserAnalyzeRequest, queryWrapper);
//
// 		// 分析维度：每日、每周、每月
// 		String timeDimension = spaceUserAnalyzeRequest.getTimeDimension();
// 		switch (timeDimension) {
// 			case "day":
// 				queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m-%d') AS period", "COUNT(*) AS count");
// 				break;
// 			case "week":
// 				queryWrapper.select("YEARWEEK(createTime) AS period", "COUNT(*) AS count");
// 				break;
// 			case "month":
// 				queryWrapper.select("DATE_FORMAT(createTime, '%Y-%m') AS period", "COUNT(*) AS count");
// 				break;
// 			default:
// 				throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的时间维度");
// 		}
//
// 		// 分组和排序
// 		queryWrapper.groupBy("period").orderByAsc("period");
//
// 		// 查询结果并转换
// 		List<Map<String, Object>> queryResult = pictureApplicationService.getPictureAppointFieldMaps(queryWrapper);
// 		return queryResult.stream()
// 				.map(result -> {
// 					String period = result.get("period").toString();
// 					Long count = ((Number) result.get("count")).longValue();
// 					return new SpaceUserAnalyzeResponse(period, count);
// 				})
// 				.collect(Collectors.toList());
// 	}
//
// 	/**
// 	 * 获取空间排行分析数据
// 	 *
// 	 * @param spaceRankAnalyzeRequest 空间排名分析请求
// 	 * @param loginTUser              当前登录用户
// 	 * @return List<Space>
// 	 */
// 	@Override
// 	public List<TeSpace> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, TUser loginTUser) {
// 		ThrowUtils.throwIf(spaceRankAnalyzeRequest == null, ErrorCode.PARAMS_ERROR);
//
// 		// 仅管理员可查看空间排行
// 		ThrowUtils.throwIf(!loginTUser.isAdmin(), ErrorCode.NO_AUTH_ERROR, "无权查看空间排行");
//
// 		// 构造查询条件
// 		QueryWrapper<TeSpace> queryWrapper = new QueryWrapper<>();
// 		queryWrapper.select("id", "spaceName", "userId", "totalSize")
// 				.orderByDesc("totalSize")
// 				.last("LIMIT " + spaceRankAnalyzeRequest.getTopN()); // 取前 N 名
//
// 		// 查询结果
// 		return spaceApplicationService.getSpaceList(queryWrapper);
// 	}
//
// 	/**
// 	 * 检查用户空间分析权限
// 	 *
// 	 * @param spaceAnalyzeRequest 空间分析请求
// 	 * @param loginTUser          当前登录用户
// 	 */
// 	private void checkSpaceAnalyzeAuth(SpaceAnalyzeRequest spaceAnalyzeRequest, TUser loginTUser) {
// 		// 检查权限
// 		if (spaceAnalyzeRequest.isQueryAll() || spaceAnalyzeRequest.isQueryPublic()) {
// 			// 全空间分析或者公共图库权限校验：仅管理员可访问
// 			ThrowUtils.throwIf(!loginTUser.isAdmin(), ErrorCode.NO_AUTH_ERROR, "无权访问公共图库");
// 		} else {
// 			// 私有空间权限校验
// 			Long spaceId = spaceAnalyzeRequest.getSpaceId();
// 			ThrowUtils.throwIf(spaceId == null || spaceId <= 0, ErrorCode.PARAMS_ERROR);
// 			TeSpace teSpace = spaceApplicationService.getSpaceById(spaceId);
// 			ThrowUtils.throwIf(teSpace == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
// 			spaceApplicationService.checkSpaceAuth(teSpace, loginTUser);
// 		}
// 	}
//
// 	/**
// 	 * 根据分析范围填充分析查询条件
// 	 *
// 	 * @param spaceAnalyzeRequest 空间分析请求
// 	 * @param queryWrapper        QueryWrapper
// 	 */
// 	private static void fillAnalyzeQueryWrapper(SpaceAnalyzeRequest spaceAnalyzeRequest, QueryWrapper<TePicture> queryWrapper) {
// 		if (spaceAnalyzeRequest.isQueryAll()) {
// 			return;
// 		}
// 		if (spaceAnalyzeRequest.isQueryPublic()) {
// 			queryWrapper.isNull("spaceId");
// 			return;
// 		}
// 		Long spaceId = spaceAnalyzeRequest.getSpaceId();
// 		if (spaceId != null) {
// 			queryWrapper.eq("spaceId", spaceId);
// 			return;
// 		}
// 		throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定查询范围");
// 	}
//
// }
