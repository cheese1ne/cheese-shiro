package com.cheese.shiro.common.service;

/**
 * shiroService 实现的提供接口
 * 用于解决feign实现懒加载导致启动装配报错的问题
 * @author sobann
 */
public interface ShiroServiceProvider {
    /**
     * 获取权限实现实例
     * @return
     */
    ShiroService getShiroService();
}
