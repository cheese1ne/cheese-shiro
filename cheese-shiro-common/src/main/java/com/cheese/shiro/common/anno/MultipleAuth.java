package com.cheese.shiro.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 复合注解，对@Auth进行管理
 * 多重权限校验,内含多个@Auth
 *
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MultipleAuth {
    Auth[] value();
}
