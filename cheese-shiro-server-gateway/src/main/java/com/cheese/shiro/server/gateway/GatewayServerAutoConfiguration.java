package com.cheese.shiro.server.gateway;

import com.cheese.shiro.common.anno.Auth;
import com.cheese.shiro.common.anno.Login;
import com.cheese.shiro.common.anno.ServerUri;
import com.cheese.shiro.common.manager.token.JwtTokenManager;
import com.cheese.shiro.common.manager.token.TokenManager;
import com.cheese.shiro.common.manager.uri.entity.AuthUriMapping;
import com.cheese.shiro.common.service.ShiroServiceProvider;
import com.cheese.shiro.server.gateway.handler.HandlerManager;
import com.cheese.shiro.server.gateway.handler.auth.AuthHandler;
import com.cheese.shiro.server.gateway.handler.identity.IdentityContextHandler;
import com.cheese.shiro.server.gateway.handler.server.ServerHandler;
import com.cheese.shiro.server.gateway.handler.token.DefaultTokenHandler;
import com.cheese.shiro.server.gateway.handler.token.TokenHandler;
import com.cheese.shiro.server.gateway.props.CookieProps;
import com.cheese.shiro.server.gateway.props.GatewayProps;
import com.cheese.shiro.server.gateway.props.ServerProps;
import com.cheese.shiro.server.gateway.props.TokenProps;
import com.cheese.shiro.server.gateway.register.ServerRegisterManager;
import com.cheese.shiro.server.gateway.uri.ServerUriManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置各项bean
 * @author sobann
 */
@ComponentScan(basePackages = "com.cheese.shiro.server.gateway.props")
@Configuration
public class GatewayServerAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(GatewayServerAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public HandlerManager handlerManager(){
        return new HandlerManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenManager tokenManager(TokenProps tokenProps){
        JwtTokenManager jwtTokenManager = new JwtTokenManager();
        jwtTokenManager.setId(tokenProps.getId());
        jwtTokenManager.setKey(tokenProps.getKey());
        jwtTokenManager.setTokenName(tokenProps.getName());
        jwtTokenManager.setExpire(tokenProps.getExpire());
        logger.info("prepare to initialize JwtTokenManager");
        return jwtTokenManager;
    }

    /*************************ServerHandler 配置 ******************************************/


    /**
     * 开启 @serverUri 校验器
     * @param serverProps
     * @param serverRegisterManager 注解配置
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerHandler ServerHandler(ServerProps serverProps, GatewayProps gatewayProps, ServerRegisterManager serverRegisterManager){
        ServerHandler serverRequestChecker = new ServerHandler();
        serverRequestChecker.setShiroServerHeader(serverProps.getHeader());
        serverRequestChecker.setShiroServerIdentity(serverProps.getValue());
        serverRequestChecker.setUriManager(new ServerUriManager(serverRegisterManager, ServerUri.REGISTER_KEY));
        serverRequestChecker.setEnable(gatewayProps.isServer());
        logger.info("prepare to initialize ServerHandler");
        return serverRequestChecker;
    }


    /*************************TokenHandler 配置 ******************************************/
    /**
     * 开启令牌检查器
     * @param serverRegisterManager 注解配置
     * @param tokenManager 令牌服务
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public TokenHandler TokenHandler(ServerRegisterManager serverRegisterManager,
                                     TokenManager tokenManager,
                                     CookieProps cookieProps,
                                     GatewayProps gatewayProps){
        DefaultTokenHandler checker = new DefaultTokenHandler();
        checker.setTokenManager(tokenManager);
        checker.setEnableChecker(gatewayProps.isLogin());
        checker.setCookieProps(cookieProps);
        checker.setUriManager(new ServerUriManager(serverRegisterManager, Login.REGISTER_KEY));
        logger.info("prepare to initialize DefaultTokenHandler");
        return checker;
    }

    /*************************PermHandler 配置 ******************************************/
    /**
     * 开启权限检查器
     * @param shiroServiceProvider 权限查询服务实现的提供接口
     *                     网关独立，则进行远程调用；与core服务一起，则是使用服务中实例进行注入
     * @param serverRegisterManager 注解配置
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public AuthHandler PermHandler(ShiroServiceProvider shiroServiceProvider,
                                   ServerRegisterManager serverRegisterManager,
                                   GatewayProps gatewayProps){

        AuthHandler authHandler = new AuthHandler();
        //创建@Auth管理器
        authHandler.setUriManager(new ServerUriManager<AuthUriMapping>(serverRegisterManager, Auth.REGISTER_KEY));
        authHandler.setShiroServiceProvider(shiroServiceProvider);
        authHandler.setEnable(gatewayProps.isAuth());
        logger.info("prepare to initialize AuthHandler");
        return authHandler;
    }


    @Bean
    @ConditionalOnMissingBean
    public IdentityContextHandler identityContextHandler(){
        logger.info("prepare to initialize IdentityContextHandler");
        return new IdentityContextHandler();
    }
}
