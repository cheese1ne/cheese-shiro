package com.cheese.shiro.common.config;

/**
 * 服务配置(权限客户端及服务端)
 * @author sobann
 */
public class ServerConfig {
    /**
     * 服务启动状态
     */
    private boolean startUp;
    /**
     * 权限配置信息
     */
    private ShiroConfig shiroConfig;

    public ServerConfig() {
    }

    public boolean isStartUp() {
        return startUp;
    }

    public void setStartUp(boolean startUp) {
        this.startUp = startUp;
    }

    public ShiroConfig getShiroConfig() {
        return shiroConfig;
    }

    public void setShiroConfig(ShiroConfig shiroConfig) {
        this.shiroConfig = shiroConfig;
    }
}
