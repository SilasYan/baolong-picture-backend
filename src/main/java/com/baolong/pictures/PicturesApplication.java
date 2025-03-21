package com.baolong.pictures;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 启动类
 */
@SpringBootApplication
@MapperScan(value = {"com.baolong.pictures.infrastructure.persistence.**.mybatis"})
@EnableAspectJAutoProxy(exposeProxy = true) // 通过 AOP 提供对当前代理对象的访问, 通过 AopContext.currentProxy() 获取当前代理对象
@EnableAsync // 开启异步注解功能
public class PicturesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PicturesApplication.class, args);
	}

}
