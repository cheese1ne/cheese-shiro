package com.cheese.shiro.server.gateway.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务间远程调用参数配置项
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.server")
public class ServerProps {
    /**
     * 服务间远程调用头
     * 用于@ServerUri注册判断是否是内部服务
     */
    private String header="shiro";
    /**
     * 服务间远程调用头的默认值
     */
    private String value="shiroserver";

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
