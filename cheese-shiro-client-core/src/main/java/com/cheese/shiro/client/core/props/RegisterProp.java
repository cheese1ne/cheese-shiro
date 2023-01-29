package com.cheese.shiro.client.core.props;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 客户端注册配置
 *
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.register")
public class RegisterProp {

    public RegisterProp(@Autowired(required = false) ConfigurableEnvironment environment) {
        if (Objects.nonNull(environment)) {
            //@Value(${spring.application.name})，服务名称在微服务中唯一
            service = environment.getProperty("spring.application.name");
        }
    }

    /**
     * 注册方式：http、kafka、rabbit
     * 默认为http,通过rest请求注册和同步服务配置
     */
    private String type = "http";
    /**
     * 注册服务名称
     * 届时将服务配置信息通过service-InstanceConfig进行缓存
     */
    private String service;
    /**
     * 网关服务名称，借由eureka完成服务名到ip:port的映射
     * 发送请求http://zuul/shiro/register
     */
    private String server = "zuul";
    /**
     * 网关注册地址是否动态处理
     */
    private boolean serverDynamic = false;
    /**
     * 注册线程池数量
     */
    private int threadNum = 2;
    /**
     * 注册间隔
     * 在HTTP模式下生效
     */
    private int interval = 5000;
    /**
     * 配置同步检查间隔 （<=0 ,停止检查）
     * 在HTTP模式下生效
     */
    private int syncInterval = 60000;

    /**
     * 服务注册检查
     * 在HTTP模式下生效
     */
    private boolean doChecker = true;

    /**
     * 服务注册检查
     * 在HTTP模式下生效
     */
    private String checkRegisterUrl;


    public void setCheckRegisterUrl(String checkRegisterUrl) {
        this.checkRegisterUrl = checkRegisterUrl;
    }

    public String getCheckRegisterUrl() {
        return this.checkRegisterUrl;
    }

    public void setDoChecker(boolean doChecker) {
        this.doChecker = doChecker;
    }

    public boolean getDoChecker() {
        return this.doChecker;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public boolean isServerDynamic() {
        return serverDynamic;
    }

    public void setServerDynamic(boolean serverDynamic) {
        this.serverDynamic = serverDynamic;
    }

    public int getThreadNum() {
        return threadNum;
    }

    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getSyncInterval() {
        return syncInterval;
    }

    public void setSyncInterval(int syncInterval) {
        this.syncInterval = syncInterval;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
