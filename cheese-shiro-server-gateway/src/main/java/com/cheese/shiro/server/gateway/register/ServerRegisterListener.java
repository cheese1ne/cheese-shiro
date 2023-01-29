package com.cheese.shiro.server.gateway.register;

import com.cheese.shiro.common.Coder;
import com.cheese.shiro.common.domain.ClientInstance;
import com.cheese.shiro.common.manager.identity.JacksonCoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务信息注册监听器抽象类
 * @author sobann
 */
public abstract class ServerRegisterListener extends ServerRegisterManager {
    private static final Logger logger = LoggerFactory.getLogger(ServerRegisterListener.class);
    /**
     * 各服务uri信息 uriPattern
     */
    private static Map<String, ClientInstance> instances = new ConcurrentHashMap<>();

    /**
     * 数据注册时的序列化方式
     */
    protected Coder coder = new JacksonCoder();

    public Coder getCoder() {
        return coder;
    }

    public void setCoder(Coder coder) {
        this.coder = coder;
    }

    /**
     * 根据客户端权限配置信息，同步更新网关校验信息配置
     * @param clientInstance
     */
    public void registerInstance(ClientInstance clientInstance){
        //获取客户端名称
        String service = clientInstance.getService();
        instances.put(service,clientInstance);
    }

    @Override
    public ClientInstance getRegisterInstance(String service) {
        return instances.get(service);
    }
}
