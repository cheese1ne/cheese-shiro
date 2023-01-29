package com.cheese.shiro.client.core.register;

import com.cheese.shiro.client.core.manager.uri.AdditionalUriManager;
import com.cheese.shiro.client.core.util.ApplicationContextHelper;
import com.cheese.shiro.common.Coder;
import com.cheese.shiro.common.anno.*;
import com.cheese.shiro.common.component.SyncComponent;
import com.cheese.shiro.common.config.ServerConfig;
import com.cheese.shiro.common.config.ShiroConfig;
import com.cheese.shiro.common.domain.ClientInstance;
import com.cheese.shiro.common.manager.identity.JacksonCoder;
import com.cheese.shiro.common.manager.uri.UriManager;
import com.cheese.shiro.common.manager.uri.entity.AssignRouteMapping;
import com.cheese.shiro.common.manager.uri.entity.AuthUriMapping;
import com.cheese.shiro.common.manager.uri.entity.GatewayLogUriMapping;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 权限客户端注册
 *
 * @author sobann
 */
public abstract class ClientRegister implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ClientRegister.class);

    private volatile ShiroConfig shiroConfig;

    public ShiroConfig getShiroConfig() {
        return shiroConfig;
    }

    protected Coder coder = new JacksonCoder();
    protected String service;
    @Autowired
    protected ApplicationContextHelper applicationContextHelper;
    @Autowired(required = false)
    protected List<ConfigRegister> configRegisters;
    @Autowired(required = false)
    protected List<AdditionalUriManager> additionalUriManagers;
    @Autowired(required = false)
    protected List<SyncComponent> syncComponents;

    public void setService(String service) {
        this.service = service;
    }

    public Coder getCoder() {
        return coder;
    }

    public void setCoder(Coder coder) {
        this.coder = coder;
    }

    /**
     * 将配置项注册至服务端
     *
     * @param isStartup 是否处于启动状态
     */
    public void registerToServer(boolean isStartup) {
        ClientInstance clientInstance = getUriConfig();
        clientInstance.setStartUp(isStartup);
        registerToServer(clientInstance);
        //检查是否有附带uri信息，并进行发送
        registerAdditionToServer();
    }

    public ClientInstance getUriConfig() {
        ClientInstance clientInstance = new ClientInstance();
        clientInstance.setService(service);
        Map<String, Object> configs = new ConcurrentHashMap<>();
        if (configRegisters != null) {
            configRegisters.forEach(
                    register -> {
                        Map<String, Object> config = register.getRegisterConfig();
                        if (!CollectionUtils.isEmpty(config)) {
                            configs.putAll(config);
                        }
                    });
        }
        clientInstance.setConfig(configs);
        return clientInstance;
    }

    /**
     * 根据shiroGateway配置，对客户端CurrentIdentityManager及TokenManager进行配置同步
     *
     * @param shiroConfig
     */
    public void syncConfigWithServer(ShiroConfig shiroConfig) {
        this.shiroConfig = shiroConfig;
        if (syncComponents != null) {
            for (SyncComponent syncComponent : syncComponents) {
                try {
                    syncComponent.sync(shiroConfig);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 查看是否存在其他服务信息，并进行注册
     * 其他额外服务 startup设置为false
     */
    public void registerAdditionToServer() {
        if (!CollectionUtils.isEmpty(additionalUriManagers)) {
            logger.info("Find Addition UriManager,send These Addtional Config to Gateway");
            for (AdditionalUriManager manager : additionalUriManagers) {
                List<String> appNames = manager.getAppNames();
                if (CollectionUtils.isEmpty(appNames)) {
                    continue;
                }
                for (String appName : appNames) {
                    if (StringUtils.isNotBlank(appName)) {
                        ClientInstance clientInstance = getServiceConfig(false, appName, manager);
                        registerToServer(clientInstance);
                    }
                }
            }
        }
    }


    /**
     * 使用指定AdditionalUriManager获取信息进行注册
     *
     * @param isStartup
     * @param appName
     * @param manager
     */
    public ClientInstance getServiceConfig(boolean isStartup, String appName, AdditionalUriManager manager) {
        ClientInstance clientInstance = new ClientInstance();
        clientInstance.setStartUp(isStartup);
        clientInstance.setService(appName);
        Map<String, Object> configs = new ConcurrentHashMap<>();
        List<AuthUriMapping> authUriMappings = manager.getAuthUri(appName);
        if (!CollectionUtils.isEmpty(authUriMappings)) {
            configs.put(Auth.REGISTER_KEY, authUriMappings);
        }
        List<UriMapping> loginUriMappings = manager.getLoginUri(appName);
        if (!CollectionUtils.isEmpty(loginUriMappings)) {
            configs.put(Login.REGISTER_KEY, loginUriMappings);
        }
        List<UriMapping> serverUriMappings = manager.getServerUri(appName);
        if (!CollectionUtils.isEmpty(serverUriMappings)) {
            configs.put(ServerUri.REGISTER_KEY, serverUriMappings);
        }
        List<UriMapping> uriPattern = manager.getUriPattern(appName);
        if (!CollectionUtils.isEmpty(uriPattern)) {
            configs.put(UriManager.REGISTER_PATTERN_PATTERN, uriPattern);
        }
        List<GatewayLogUriMapping> gatewayLogUriMappings = manager.getGatewayLogUri(appName);
        if (!CollectionUtils.isEmpty(gatewayLogUriMappings)) {
            configs.put(GatewayLog.REGISTER_KEY, gatewayLogUriMappings);
        }
        List<AssignRouteMapping> assignRouteMappings = manager.getAssignRoutUri(appName);
        if (!CollectionUtils.isEmpty(assignRouteMappings)) {
            configs.put(AssignRoute.REGISTER_KEY, assignRouteMappings);
        }
        clientInstance.setConfig(configs);
        return clientInstance;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        registerToServer(true);
    }


    public abstract void registerToServer(ClientInstance config);

    /**
     * 刷新消息
     * 实现类该接口，调用syncConfigWithServer
     * 实现信息接受和刷新
     *
     * @param serverConfig
     */
    public abstract void refreshConfigWithServer(ServerConfig serverConfig);

}
