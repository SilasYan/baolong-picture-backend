// package com.baolong.pictures.application.service;
//
// import com.baolong.pictures.domain.space.entity.TeSpace;
// import com.baolong.pictures.domain.user.entity.TUser;
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
//
// import java.util.List;
//
// /**
//  * 空间分析服务
//  *
//  * @author Baolong 2025年02月27 19:17
//  * @version 1.0
//  * @since 1.8
//  */
// public interface SpaceAnalyzeApplicationService {
//
// 	/**
// 	 * 获取空间使用分析数据
// 	 *
// 	 * @param spaceUsageAnalyzeRequest 空间资源分析请求
// 	 * @param loginTUser                当前登录用户
// 	 * @return SpaceUsageAnalyzeResponse
// 	 */
// 	SpaceUsageAnalyzeResponse getSpaceUsageAnalyze(SpaceUsageAnalyzeRequest spaceUsageAnalyzeRequest, TUser loginTUser);
//
// 	/**
// 	 * 获取空间分类分析数据
// 	 *
// 	 * @param spaceCategoryAnalyzeRequest 空间分类分析请求
// 	 * @param loginTUser                   当前登录用户
// 	 * @return List<SpaceCategoryAnalyzeResponse>
// 	 */
// 	List<SpaceCategoryAnalyzeResponse> getSpaceCategoryAnalyze(SpaceCategoryAnalyzeRequest spaceCategoryAnalyzeRequest, TUser loginTUser);
//
// 	/**
// 	 * 获取空间标签分析数据
// 	 *
// 	 * @param spaceTagAnalyzeRequest 空间标签分析请求
// 	 * @param loginTUser              当前登录用户
// 	 * @return List<SpaceTagAnalyzeResponse>
// 	 */
// 	List<SpaceTagAnalyzeResponse> getSpaceTagAnalyze(SpaceTagAnalyzeRequest spaceTagAnalyzeRequest, TUser loginTUser);
//
// 	/**
// 	 * 获取空间大小分析数据
// 	 *
// 	 * @param spaceSizeAnalyzeRequest 空间大小分析请求
// 	 * @param loginTUser               当前登录用户
// 	 * @return List<SpaceSizeAnalyzeResponse>
// 	 */
// 	List<SpaceSizeAnalyzeResponse> getSpaceSizeAnalyze(SpaceSizeAnalyzeRequest spaceSizeAnalyzeRequest, TUser loginTUser);
//
// 	/**
// 	 * 获取空间用户上传分析数据
// 	 *
// 	 * @param spaceUserAnalyzeRequest 空间用户上传分析请求
// 	 * @param loginTUser               当前登录用户
// 	 * @return List<SpaceUserAnalyzeResponse>
// 	 */
// 	List<SpaceUserAnalyzeResponse> getSpaceUserAnalyze(SpaceUserAnalyzeRequest spaceUserAnalyzeRequest, TUser loginTUser);
//
// 	/**
// 	 * 获取空间排行分析数据
// 	 *
// 	 * @param spaceRankAnalyzeRequest 空间排名分析请求
// 	 * @param loginTUser               当前登录用户
// 	 * @return List<Space>
// 	 */
// 	List<TeSpace> getSpaceRankAnalyze(SpaceRankAnalyzeRequest spaceRankAnalyzeRequest, TUser loginTUser);
// }
