package com.cheese.shiro.server.gateway.uri;


import com.cheese.shiro.common.manager.uri.UriManager;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;
import com.cheese.shiro.server.gateway.register.ServerRegisterManager;

import java.util.ArrayList;
import java.util.Collection;

/**
 * uri管理器
 * 负责校验请求uri是否匹配
 * 以及获取 与请求匹配
 * 对应 @Auth @Login @ServerUri 使用不同的管理器，注意进行区分
 *
 * @param <E> UriMapping的泛型上界
 * @author sobann
 */
public class ServerUriManager<E> extends UriManager {

    private Collection empty = new ArrayList();

    private ServerRegisterManager serverRegisterManager;
    private String registerKey;

    public ServerUriManager(ServerRegisterManager serverRegisterManager, String registerKey) {
        this.serverRegisterManager = serverRegisterManager;
        this.registerKey = registerKey;
    }

    public ServerRegisterManager getServerRegisterManager() {
        return serverRegisterManager;
    }

    @Override
    public Collection<E> getUriMappings(String serviceId) {
        return (Collection<E>) serverRegisterManager.getRegisterInstanceConfig(serviceId, registerKey, empty);
    }

    @Override
    public Collection<UriMapping> getUriPatterns(String serviceId) {
        return (Collection<UriMapping>) serverRegisterManager.getRegisterInstanceConfig(serviceId, REGISTER_PATTERN_PATTERN, empty);
    }

}
