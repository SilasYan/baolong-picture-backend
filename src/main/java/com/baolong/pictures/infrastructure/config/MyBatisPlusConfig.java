package com.baolong.pictures.infrastructure.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

/**
 * MyBatisPlus 分页插件配置
 */
@Configuration
// @MapperScan("com.baolong.pictures.infrastructure.mapper")  // 这里在启动类时已经注入
public class MyBatisPlusConfig implements MetaObjectHandler {

	/**
	 * 拦截器配置
	 */
	@Bean
	public MybatisPlusInterceptor mybatisPlusInterceptor() {
		MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
		// 分页插件
		interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
		return interceptor;
	}

	/**
	 * 新增时的字段填充
	 *
	 * @param metaObject 元对象
	 */
	@Override
	public void insertFill(MetaObject metaObject) {

	}

	/**
	 * 更新时的字段填充
	 *
	 * @param metaObject 元对象
	 */
	@Override
	public void updateFill(MetaObject metaObject) {
		this.setFieldValByName("editTime", new Date(), metaObject);
	}
}
