package com.cheese.shiro.server.gateway.props;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * cookie配置项
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.cookie")
public class CookieProps {
    private String domain="/";
    private String path="/";
    private boolean enable = true;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }
}
