package com.cheese.shiro.server.gateway.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务间线程身份信息传递参数配置项
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.identity")
public class IdentityProps {
    /**
     * 服务间传递参数名称
     */
    private String name="context_tracer";
    /**
     * 默认传递参数值
     */
    private String defaultValue ="-1";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}
