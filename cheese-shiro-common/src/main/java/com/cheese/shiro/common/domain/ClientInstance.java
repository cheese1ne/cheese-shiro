package com.cheese.shiro.common.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * 客户端实例配置
 *
 * @author sobann
 */
public class ClientInstance implements Serializable {
    private static final long serialVersionUID = 6556170503455724112L;

    /**
     * 是否为启动状态
     */
    private boolean startUp;
    /**
     * 服务名称
     */
    private String service;
    /**
     * 服务的配置信息，通过获取已注入IOC的BEAN
     * 服务本身的配置信息
     * 服务中的自定义注解信息
     */
    private Map<String, Object> config;

    public boolean isStartUp() {
        return startUp;
    }

    public void setStartUp(boolean startUp) {
        this.startUp = startUp;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Map<String, Object> getConfig() {
        return config;
    }

    public void setConfig(Map<String, Object> config) {
        this.config = config;
    }
}
