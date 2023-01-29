package com.cheese.shiro.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * controller 服务调用标志
 * 服务之间调用的标识，仅网关处使用
 * 使用后，不使用服务标识，拒绝访问
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface ServerUri {
    public static final String REGISTER_KEY ="Server_URI";
}
