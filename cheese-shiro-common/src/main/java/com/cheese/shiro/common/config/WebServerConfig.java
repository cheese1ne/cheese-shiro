package com.cheese.shiro.common.config;

import java.util.List;

/**
 * web服务配置
 * @author sobann
 */
public class WebServerConfig extends ServerConfig {

    /**
     * 网关实例地址列表
     */
    private List<String> servers;

    public WebServerConfig() {
        super();
    }

    public WebServerConfig(ServerConfig serverConfig) {
        super();
        setShiroConfig(serverConfig.getShiroConfig());
        setStartUp(serverConfig.isStartUp());
    }


    public List<String> getServers() {
        return servers;
    }

    public void setServers(List<String> servers) {
        this.servers = servers;
    }
}
