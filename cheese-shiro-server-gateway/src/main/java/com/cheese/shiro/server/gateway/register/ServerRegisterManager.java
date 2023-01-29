package com.cheese.shiro.server.gateway.register;

import com.cheese.shiro.common.config.ServerConfig;
import com.cheese.shiro.common.config.ShiroConfig;
import com.cheese.shiro.common.domain.ClientInstance;
import com.cheese.shiro.common.manager.identity.IdentityManager;
import com.cheese.shiro.server.gateway.props.IdentityProps;
import com.cheese.shiro.server.gateway.props.TokenProps;
import org.springframework.beans.factory.InitializingBean;

/**
 * 服务注册
 * @author sobann
 */
public abstract class ServerRegisterManager implements InitializingBean {

    private IdentityProps identityProps;
    private TokenProps tokenProps;

    public void setIdentityProps(IdentityProps identityProps) {
        this.identityProps = identityProps;
        IdentityManager.configure(identityProps.getName(), identityProps.getDefaultValue());
    }

    public void setTokenProps(TokenProps tokenProps) {
        this.tokenProps = tokenProps;
    }

    /**
     * 获取服务实例的配置信息
     * @param service
     * @param registerKey
     * @param defaultValue
     * @return
     */
    public Object getRegisterInstanceConfig(String service, String registerKey, Object defaultValue){
        ClientInstance clientInstance = getRegisterInstance(service);
        if(clientInstance==null){
            return defaultValue;
        }
        return clientInstance.getConfig().getOrDefault(registerKey,defaultValue);
    }

    public void syncConfigToClient(boolean isStartUp){
        ServerConfig serverConfig = getServerConfig();
        serverConfig.setStartUp(isStartUp);
        syncConfigToClient(serverConfig);
    }

    public ServerConfig getServerConfig(){
        ShiroConfig config = new ShiroConfig();
        config.setDefaultIdentity(identityProps.getDefaultValue());
        config.setContextTracerName(identityProps.getName());

        config.setTokenName(tokenProps.getName());
        config.setTokenId(tokenProps.getId());
        config.setTokenKey(tokenProps.getKey());
        config.setExpire(tokenProps.getExpire());
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setShiroConfig(config);
        return serverConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        syncConfigToClient(true);
    }

    /**
     * 刷新消息
     * 实现类该接口，调用registerInstance
     * 实现信息接受和刷新
     * @param clientInstance
     */
    public abstract void register(ClientInstance clientInstance);

    /**
     * 获取注册的服务实例
     * @param service
     * @return
     */
    public abstract ClientInstance getRegisterInstance(String service);

    /**
     * 同步配置至客户端
     * @param shiroConfig
     */
    public abstract void syncConfigToClient(ServerConfig shiroConfig);
}
