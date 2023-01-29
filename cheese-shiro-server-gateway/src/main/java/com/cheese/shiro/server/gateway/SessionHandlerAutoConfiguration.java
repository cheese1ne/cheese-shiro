package com.cheese.shiro.server.gateway;

import com.cheese.shiro.common.manager.session.SessionStoreManager;
import com.cheese.shiro.server.gateway.handler.session.SessionHandler;
import com.cheese.shiro.session.manager.RedisSessionStoreManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;

/**
 * 配合cheese-shiro-session中定义的组件注册session处理器
 * @author sobann
 */
@Configuration
public class SessionHandlerAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SessionHandlerAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public SessionStoreManager sessionStoreManager(RedisConnectionFactory redisConnectionFactory){
        logger.info("prepare to initialize RedisSessionStoreManager");
        return new RedisSessionStoreManager(redisConnectionFactory);
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionHandler sessionHandler(SessionStoreManager sessionStoreManager){
        logger.info("prepare to initialize SessionHandler");
        return new SessionHandler(sessionStoreManager);
    }
}
