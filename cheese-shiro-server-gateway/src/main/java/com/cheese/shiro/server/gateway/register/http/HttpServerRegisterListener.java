package com.cheese.shiro.server.gateway.register.http;


import com.cheese.shiro.common.config.ServerConfig;
import com.cheese.shiro.common.domain.ClientInstance;
import com.cheese.shiro.server.gateway.register.ServerRegisterListener;

import java.util.List;

/**
 * 服务注册器单个实例的HTTP实现
 * @author sobann
 */
public class HttpServerRegisterListener extends ServerRegisterListener {

    @Override
    public void syncConfigToClient(ServerConfig shiroConfig) {
    }

    @Override
    public void register(ClientInstance clientInstance) {
        registerInstance(clientInstance);
    }

    public List<String> getServers(){
        return null;
    }

    /**
     * 刷新 服务实例配置
     * @param service
     */
    public void refreshInstanceConfig(String service){
    }
}
