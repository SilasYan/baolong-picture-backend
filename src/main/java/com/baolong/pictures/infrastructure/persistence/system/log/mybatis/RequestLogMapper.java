package com.baolong.pictures.infrastructure.persistence.system.log.mybatis;

import com.baolong.pictures.infrastructure.persistence.system.log.entity.RequestLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* 请求日志表 (request_log) - 操作数据库 Mapper 接口
*
* @author Baolong 2025-03-08 01:39:44
*/
public interface RequestLogMapper extends BaseMapper<RequestLog> {
}
