package com.cheese.shiro.server.gateway;

import com.cheese.shiro.rpc.ShiroRpcAutoImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 复合注解，用于加载Rpc、各类处理器、网关相关配置、服务注册器相关配置
 * 开启权限网关
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import({GatewayServerAutoConfiguration.class,
        GatewayServerAutoImportSelector.class,
        ShiroRpcAutoImportSelector.class,
        RegisterAutoImportSelector.class})
public @interface EnableShiroGateway {

}
