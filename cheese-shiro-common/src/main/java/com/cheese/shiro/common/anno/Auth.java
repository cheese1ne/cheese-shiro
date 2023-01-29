package com.cheese.shiro.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * controller 类的权限控制标识
 * 默认需要@Login前置
 * 需要权限验证的均需登陆使用
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auth {
	public static final String REGISTER_KEY ="Auth_URI";
	//操作标识符
	String identifier();
	//实例id 的参数名，使用默认则代表不检查实例，仅检查方法
	String instanceId() default "_";
	//是否检查登陆
	boolean login() default true;
	//instanceId 参数正则表达式，负责参数提取
	String regex() default "";
	// index 正则匹配号，获取第几位
	int index() default 1;
	//所属模块，决定获取权限的范围
	String app() default ""; //* 代表所有
}
