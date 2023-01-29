package com.cheese.shiro.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 网关路由指派
 * 指定assginKey,网关将将根据key,对可用服务进行hash环取值
 * 服务实例列表不变的情况下，同一key指定分发至同一服务实例上
 * 服务实例列表变化的情况下，最大限度保证分发值同一实例上
 *
 * assignKey,需要在请求头定义（暂定）
 * @author sobann
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AssignRoute {
    public static final String REGISTER_KEY ="AssignRoute_URI";
    String assignKey() default "AssignKey";
}
