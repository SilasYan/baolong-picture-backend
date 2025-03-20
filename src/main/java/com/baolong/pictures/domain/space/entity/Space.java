package com.baolong.pictures.domain.space.entity;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baolong.pictures.domain.space.enums.SpaceLevelEnum;
import com.baolong.pictures.domain.space.enums.SpaceTypeEnum;
import com.baolong.pictures.infrastructure.exception.BusinessException;
import com.baolong.pictures.infrastructure.exception.ErrorCode;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 空间表
 *
 * @TableName space
 */
@TableName(value = "space")
@Data
public class Space implements Serializable {

	// region 属性

	/**
	 * 主键 ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * 空间名称
	 */
	private String spaceName;

	/**
	 * 空间类型（0-私有空间, 1-团队空间）
	 */
	private Integer spaceType;

	/**
	 * 空间级别（0-普通版, 1-专业版, 2-旗舰版）
	 */
	private Integer spaceLevel;

	/**
	 * 空间图片最大大小（单位: B）
	 */
	private Long maxSize;

	/**
	 * 空间图片最大数量（单位: 张）
	 */
	private Long maxCount;

	/**
	 * 空间使用大小（单位: B）
	 */
	private Long usedSize;

	/**
	 * 空间使用数量（单位: 张）
	 */
	private Long usedCount;

	/**
	 * 创建用户 ID
	 */
	private Long userId;

	/**
	 * 是否删除
	 */
	private Integer isDelete;

	/**
	 * 编辑时间
	 */
	@TableField(value = "edit_time", fill = FieldFill.UPDATE)
	private Date editTime;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新时间
	 */
	private Date updateTime;

	@TableField(exist = false)
	private static final long serialVersionUID = 1L;

	// endregion 属性

	// region 行为

	/**
	 * 校验空间大小和总数
	 */
	public void validSpaceSizeAndCount() {
		if (this.getUsedSize() >= this.getMaxSize()) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间大小不足");
		}
		if (this.getUsedCount() >= this.getMaxSize()) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "空间数量不足");
		}
	}

	/**
	 * 校验空间激活请求
	 */
	public void validSpaceActivateAndAddRequest() {
		if (StrUtil.isEmpty(this.getSpaceName())) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称不能为空");
		}
		if (this.spaceName.length() > 30) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
		}
		if (this.spaceLevel == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不能为空");
		}
		if (this.spaceType == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间类型不能为空");
		}
		if (this.maxSize == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间大小不能为空");
		}
		if (this.maxCount == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间数量不能为空");
		}
	}

	/**
	 * 填充默认值
	 */
	public void fillSpaceDefaultValue() {
		if (StrUtil.isEmpty(this.getSpaceName())) {
			this.setSpaceName("个人空间_" + RandomUtil.randomString(6));
		}
		if (this.spaceType == null) {
			this.setSpaceType(SpaceTypeEnum.PRIVATE.getKey());
		}
		if (this.spaceLevel == null) {
			this.setSpaceLevel(SpaceLevelEnum.COMMON.getKey());
		}
		// 填充空间大小和数量
		SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(this.spaceLevel);
		if (spaceLevelEnum == null) throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "空间等级不存在");
		if (this.maxSize == null) {
			this.setMaxSize(spaceLevelEnum.getMaxSize());
		}
		if (this.maxCount == null) {
			this.setMaxCount(spaceLevelEnum.getMaxCount());
		}
	}

	// endregion 行为
}
