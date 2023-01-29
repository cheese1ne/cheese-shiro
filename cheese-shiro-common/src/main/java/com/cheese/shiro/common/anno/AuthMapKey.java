package com.cheese.shiro.common.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多条件查询，可参考@AuthKey，即单一条件权限过滤情况
 *
 * keys sql需要过滤的 字段名称，sql语句的列名（主键），在Sql语句的原始形态
 * entity 查询语句对应的实体
 * scopes  如果是实体某种范围，如获取权限内用户所在的用户组等
 * action 针对某项操作，默认为get
 * ignoreLevel 查询范围时，是否允许小范围向大范围转化
 * app 所属模块
 *
 * @author sobann
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AuthMapKey {
    String entity();//实体类型
    String action() default "get";
    String[] keys();//对应sql key
    String[] scopes();//对应数据类型
    String app() default "";//对应模块，决定获取权限的范围，
}
