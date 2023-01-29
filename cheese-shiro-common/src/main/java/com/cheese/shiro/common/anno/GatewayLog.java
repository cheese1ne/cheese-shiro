package com.cheese.shiro.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 网关统一日志记录注解
 * 用于记录经由网关转发的请求
 *
 * entity 对应实体
 * action 对应操作
 * instanceId 对应实体id
 * requestBody 是否记录请求体 (上传接口禁止)
 * responseBody 是否记录响应体(非json接口禁止)
 *
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface GatewayLog {
    public static final String REGISTER_KEY = "GatewayLog_URI";

    String entity() default "";

    String action() default "";

    String instanceId() default "";

    boolean requestBody() default true;

    boolean responseBody() default true;
}
