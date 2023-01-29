package com.cheese.shiro.client.core;

import com.cheese.shiro.client.core.checker.identity.SpringSyncIdentityManagerChecker;
import com.cheese.shiro.client.core.config.IdentityInterceptorConfiguration;
import com.cheese.shiro.client.core.interceptor.RestTemplateInterceptor;
import com.cheese.shiro.client.core.manager.uri.*;
import com.cheese.shiro.client.core.props.AuthProp;
import com.cheese.shiro.client.core.util.ApplicationContextHelper;
import com.cheese.shiro.common.manager.identity.IdentityManagerChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * 权限客户端，默认配置类
 * @author sobann
 */
@ComponentScan(basePackages = "com.cheese.shiro.client.core.props")
@Configuration
public class ShiroClientAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ShiroClientAutoConfiguration.class);

    @Primary
    @Bean
    public ApplicationContextHelper applicationContextHelper() {
        logger.info("prepare to initialize ApplicationContextHelper");
        return new ApplicationContextHelper();
    }

    /**
     * identityManager 配置检查,自动同步配置
     */
    @Bean
    @ConditionalOnMissingBean(IdentityManagerChecker.class)
    public SpringSyncIdentityManagerChecker springSyncIdentityManagerChecker() {
        logger.info("prepare to initialize SpringSyncIdentityManagerChecker");
        return new SpringSyncIdentityManagerChecker();
    }

    /**
     * 配置 springMvc下的 identity 接收
     */
    @Bean
    @ConditionalOnMissingBean
    public IdentityInterceptorConfiguration identityHandlerInterceptorConfiguration() {
        logger.info("prepare to initialize IdentityInterceptorConfiguration");
        return new IdentityInterceptorConfiguration();
    }

    /**
     * rest template identity传递
     */
    @Bean
    public RestTemplateInterceptor restTemplateInterceptor() {
        logger.info("prepare to initialize RestTemplateInterceptor");
        return new RestTemplateInterceptor();
    }

    /**
     * 创建 权限信息管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthUriManager authUriManager(ApplicationContextHelper applicationContextHelper, AuthProp authProp) {
        AuthUriManager authUriManager = new AuthUriManager();
        authUriManager.setApplicationContextHelper(applicationContextHelper);
        authUriManager.setDefaultApp(authProp.getDefaultApp());
        logger.info("prepare to initialize AuthUriManager");
        return authUriManager;
    }

    /**
     * 创建 登陆信息管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public LoginUriManager loginUriManager(ApplicationContextHelper applicationContextHelper) {
        LoginUriManager loginUriManager = new LoginUriManager();
        loginUriManager.setApplicationContextHelper(applicationContextHelper);
        logger.info("prepare to initialize LoginUriManager");
        return loginUriManager;
    }

    /**
     * 创建 服务信息管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerUriManager serverUriManager(ApplicationContextHelper applicationContextHelper) {
        ServerUriManager serverUriManager = new ServerUriManager();
        serverUriManager.setApplicationContextHelper(applicationContextHelper);
        logger.info("prepare to initialize ServerUriManager");
        return serverUriManager;
    }

    /**
     * 创建 网关日志管理器
     */
    @Bean
    @ConditionalOnMissingBean
    public GatewayLogUriManager gatewayLogUriManager(ApplicationContextHelper applicationContextHelper) {
        GatewayLogUriManager gatewayLogUriManager = new GatewayLogUriManager();
        gatewayLogUriManager.setApplicationContextHelper(applicationContextHelper);
        logger.info("prepare to initialize GatewayLogUriManager");
        return gatewayLogUriManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public AssignRouteUriManager assignRouteUriManager(ApplicationContextHelper applicationContextHelper) {
        AssignRouteUriManager assignRouteUriManager = new AssignRouteUriManager();
        assignRouteUriManager.setApplicationContextHelper(applicationContextHelper);
        logger.info("prepare to initialize AssignRouteUriManager");
        return assignRouteUriManager;
    }
}
