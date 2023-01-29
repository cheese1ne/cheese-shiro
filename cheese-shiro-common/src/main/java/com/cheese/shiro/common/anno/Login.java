package com.cheese.shiro.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * controller层 身份确认标识 默认需要登陆前置 @Auth
 * 需要非游客权限才能进行接口访问
 *
 * @author sobann
 * 标注在Controller上，代表所有接口均需要登陆确认
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Login {
    public static final String REGISTER_KEY = "Login_URI";
}
