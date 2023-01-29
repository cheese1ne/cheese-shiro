package com.cheese.shiro.server.gateway.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * token参数配置项
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.token")
public class TokenProps {
    /**
     * 解析token的key
     * 在JWT中用于创建SecretKey，用于写入签名信息
     */
    private  String key ="com.cheese.shiro";
    private  String id="com.cheese.shiro";
    private long expire =120;
    private  String name="token";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
