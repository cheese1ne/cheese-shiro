package com.cheese.shiro.common.constant;

/**
 * 队列名称池
 * 用于消息队列名称的配置
 * @author sobann
 */
public class QueuePool {

    /**
     * 权限客户端队列名
     */
    public final static String CONFIG_INFO_CLIENT = "shiro.config.client";

    /**
     * 权限服务端队列名
     */
    public final static String CONFIG_INFO_SERVER = "shiro.config.server";

    /**
     * 权限客户端订阅队列名
     */
    public final static String CONFIG_INFO_CLIENT_FANOUT = "shiro.config.client.fanout";

    /**
     * 权限服务端订阅队列名
     */
    public final static String CONFIG_INFO_SERVER_FANOUT = "shiro.config.server.fanout";

    /**
     * AuthUri信息队列名
     */
    public final static String AUTH_URI_CONFIG = "AuthUri";

    /**
     * AuthUri信息队列名
     */
    public final static String LOGIN_URI_CONFIG = "LoginUri";

    /**
     * ServerUri信息队列名
     */
    public final static String SERVER_URI_CONFIG = "ServerUri";

    /**
     * GatewayLogUri信息队列名
     */
    public final static String GATEWAY_LOG_URI_CONFIG = "GatewayLogUri";

    /**
     * Uri信息队列名
     */
    public final static String URI_CONFIG = "Uri";

    /**
     * 服务名称配置队列名
     */
    public final static String APP_NAME_CONFIG = "AppName";

    /**
     * 服务启动状态
     */
    public final static String STARTUP = "StartUP";

    /**
     * 权限配置队列名
     */
    public final static String SHIRO_CONFIG = "ShiroConfig";
}

