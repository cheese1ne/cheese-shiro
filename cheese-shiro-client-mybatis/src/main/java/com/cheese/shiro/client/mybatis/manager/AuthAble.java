package com.cheese.shiro.client.mybatis.manager;

/**
 * mybatisPlus中实现此接口，构建一个自带auth条件的wrapper
 *
 * @author sobann
 */
public interface AuthAble {
    /**
     * 查询sql是否开启权限
     * @return
     */
    boolean isAuth();
}
