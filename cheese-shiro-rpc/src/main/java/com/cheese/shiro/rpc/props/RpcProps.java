package com.cheese.shiro.rpc.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * rpc参数配置项
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.rpc")
public class RpcProps {
    /**
     * rpc调用方式：feign/dubbo
     */
    private String type = "";
    /**
     * rpc调用的服务名
     * 默认为perm
     */
    private String server = "perm";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }
}
