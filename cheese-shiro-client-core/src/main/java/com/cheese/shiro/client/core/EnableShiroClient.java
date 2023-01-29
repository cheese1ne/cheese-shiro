package com.cheese.shiro.client.core;

import com.cheese.shiro.rpc.ShiroRpcAutoImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 复合注解，用于加载权限注册器，URI信息管理器，rpc方式
 * 开启权限网关
 *
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({ShiroClientAutoConfiguration.class,
        ShiroClientAutoImportSelector.class,
        ShiroRpcAutoImportSelector.class})
public @interface EnableShiroClient {
}
