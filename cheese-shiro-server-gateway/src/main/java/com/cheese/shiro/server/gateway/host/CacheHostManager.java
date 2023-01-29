package com.cheese.shiro.server.gateway.host;

/**
 * 主机信息缓存管理器
 * @author sobann
 */
public interface CacheHostManager {

    /**
     * 获取主机信息
     * @param assignKey
     * @return
     */
    Host gainCache(String assignKey);

    /**
     * 创建主机缓存
     * @param assignKey
     * @param host
     */
    void createCache(String assignKey, Host host);

    /**
     * 清除主机缓存
     * @param assignKey
     */
    void clearCache(String assignKey);

}
