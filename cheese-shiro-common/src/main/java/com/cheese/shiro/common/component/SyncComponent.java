package com.cheese.shiro.common.component;


import com.cheese.shiro.common.config.ShiroConfig;

/**
 * 配置同步组件
 *
 * @author sobann
 */
public interface SyncComponent {

    /**
     * 同步配置
     *
     * @param shiroConfig
     */
    void sync(ShiroConfig shiroConfig);
}
