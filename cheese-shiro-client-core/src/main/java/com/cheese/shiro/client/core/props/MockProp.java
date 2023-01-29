package com.cheese.shiro.client.core.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 屏蔽权限调用，测试时使用
 *
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.mock")
public class MockProp {
    private boolean enable = false;
    private String context;
    private String name = "context_tracer";
    private String defaultValue = "-1";

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

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
