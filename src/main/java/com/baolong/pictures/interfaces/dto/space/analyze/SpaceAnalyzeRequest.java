package com.baolong.pictures.interfaces.dto.space.analyze;

import lombok.Data;

import java.io.Serializable;

/**
 * 空间分析请求（公共）
 */
@Data
public class SpaceAnalyzeRequest implements Serializable {

    /**
     * 空间 ID
     */
    private Long spaceId;

    /**
     * 是否查询公共图库
     */
    private boolean queryPublic;

    /**
     * 全空间分析
     */
    private boolean queryAll;

    private static final long serialVersionUID = 1L;
}
