package com.cheese.shiro.server.gateway.props;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 网关参数配置项
 * @author sobann
 */
@Component
@ConfigurationProperties("cheese.shiro.gateway")
public class GatewayProps {
    /**
     * 网关类型：zuul/gateway
     * 默认为zuul网关
     */
    private String type ="zuul";
    /**
     * 是否开启@GatewayLog功能
     */
    private boolean log = true;
    /**
     * 是否开启@Assgin功能
     */
    private boolean assign = true;
    /**
     * 是否开启@Login功能
     */
    private boolean login = true;
    /**
     * 是否开启@ServerUri功能
     */
    private boolean server = true;
    /**
     * 是否开启@Auth功能
     */
    private boolean auth = true;
    /**
     * 是否使用Session保存用户信息
     */
    private boolean session = true;
    /**
     * 服务注册方式：http/kafka/rabbit
     * 默认为HTTP，通过HTTP暴露端口进行服务信息的注册
     */
    private String register ="http";

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLog() {
        return log;
    }

    public void setLog(boolean log) {
        this.log = log;
    }

    public boolean isAssign() {
        return assign;
    }

    public void setAssign(boolean assign) {
        this.assign = assign;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public boolean isServer() {
        return server;
    }

    public void setServer(boolean server) {
        this.server = server;
    }

    public boolean isAuth() {
        return auth;
    }

    public void setAuth(boolean auth) {
        this.auth = auth;
    }

    public boolean isSession() {
        return session;
    }

    public void setSession(boolean session) {
        this.session = session;
    }
}
