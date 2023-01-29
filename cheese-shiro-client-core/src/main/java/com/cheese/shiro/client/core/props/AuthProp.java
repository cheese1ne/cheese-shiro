package com.cheese.shiro.client.core.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 权限客户端配置
 *
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.auth")
public class AuthProp {

    /**
     * 开启客户端@Auth验证
     */
    private boolean enableAuth = true;
    /**
     * 开启客户端@Login验证
     */
    private boolean enableLogin = true;
    /**
     * 开启客户端@ServerUri验证
     */
    private boolean enableServerUri = true;
    /**
     * 开启客户端@GatewayLog记录
     */
    private boolean enableGatewayLog = true;
    /**
     * 开启客户端@GatewayLog记录
     */
    private boolean enableAssignRoute = true;
    /**
     * 默认模块，不进行条件过滤
     */
    private String defaultApp = "*";


    public boolean isEnableAuth() {
        return enableAuth;
    }

    public void setEnableAuth(boolean enableAuth) {
        this.enableAuth = enableAuth;
    }

    public boolean isEnableLogin() {
        return enableLogin;
    }

    public void setEnableLogin(boolean enableLogin) {
        this.enableLogin = enableLogin;
    }

    public boolean isEnableServerUri() {
        return enableServerUri;
    }

    public void setEnableServerUri(boolean enableServerUri) {
        this.enableServerUri = enableServerUri;
    }

    public boolean isEnableGatewayLog() {
        return enableGatewayLog;
    }

    public void setEnableGatewayLog(boolean enableGatewayLog) {
        this.enableGatewayLog = enableGatewayLog;
    }

    public boolean isEnableAssignRoute() {
        return enableAssignRoute;
    }

    public void setEnableAssignRoute(boolean enableAssignRoute) {
        this.enableAssignRoute = enableAssignRoute;
    }

    public String getDefaultApp() {
        return defaultApp;
    }

    public void setDefaultApp(String defaultApp) {
        this.defaultApp = defaultApp;
    }
}
