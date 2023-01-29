package com.cheese.shiro.server.gateway.register.http;

import com.cheese.shiro.server.gateway.props.IdentityProps;
import com.cheese.shiro.server.gateway.props.RegisterProps;
import com.cheese.shiro.server.gateway.props.TokenProps;
import com.cheese.shiro.server.gateway.register.ServerRegisterManager;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * HTTP的服务注册管理器配置类
 * @author sobann
 */
@Configuration
public class HttpServerRegisterManagerConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(HttpServerRegisterManagerConfiguration.class);

    @Value("${spring.cloud.client.ip-address}")
    private String ip;
    @Value("${server.port}")
    private String port;

    /**
     * 服务注册管理器
     * @param tokenProps
     * @param identityProps
     * @param redisConnectionFactory
     * @param registerConfig
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ServerRegisterManager httpRedisServerRegisterListener(TokenProps tokenProps, IdentityProps identityProps, RedisConnectionFactory redisConnectionFactory, RegisterProps registerConfig){
        String server = ip+":"+port;
        HttpRedisServerRegisterListener listener = new HttpRedisServerRegisterListener(redisConnectionFactory,server,registerConfig.getCheckThreadNum(),registerConfig.getCheckInterval());
        listener.setIdentityProps(identityProps);
        listener.setTokenProps(tokenProps);
        logger.info("prepare to initialize HttpRedisServerRegisterListener");
        return listener;
    }

    /**
     * 额外的控制层的URI注册，暴露JerseyConfig中有关网关服务注册和刷新的接口
     * @return
     */
    @Bean
    public ServletRegistrationBean jerseyServlet() {
        ServletRegistrationBean registration = new ServletRegistrationBean(new ServletContainer(), "/gateway/*");
        registration.addInitParameter(ServletProperties.JAXRS_APPLICATION_CLASS, JerseyConfig.class.getName());
        logger.info("prepare to initialize jersey expose servletBean");
        return registration;
    }
}
