package com.cheese.shiro.server.core;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 复合注解，用于加载各类管理器、权限规则解释器、shiroService的默认装饰类
 * 开启权限网关
 * 使用时注意自实现 Realm类并注入IOC中
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ShiroServerAutoConfiguration.class, ShiroServerAutoImportSelector.class})
public @interface EnableShiroServer {
}
