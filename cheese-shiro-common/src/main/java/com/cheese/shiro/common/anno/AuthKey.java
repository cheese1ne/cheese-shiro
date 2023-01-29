package com.cheese.shiro.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * dao类使用
 * key sql需要过滤的 字段名称，sql语句的列名（主键），在Sql语句的原始形态
 * entity 查询语句对应的实体
 * scope  如果是实体某种范围，如获取权限内用户所在的用户组等
 * action 针对某项操作，默认为get
 * ignoreLevel 查询范围时，是否允许小范围向大范围转化
 * app 所属模块
 * 在权限服务根据条件获取数据范围后，通过拼接IN条件语句进行权限控制
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface AuthKey {
	String key() default "";
	String entity() default "";
	String scope() default "";
	String action() default "get";
	boolean ignoreLevel() default false;
	String app() default ""; //所属模块，决定获取权限的范围，*代表所有
}
