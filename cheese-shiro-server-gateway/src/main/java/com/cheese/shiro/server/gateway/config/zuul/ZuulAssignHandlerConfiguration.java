package com.cheese.shiro.server.gateway.config.zuul;

import com.cheese.shiro.common.anno.AssignRoute;
import com.cheese.shiro.server.gateway.assign.AssignRouter;
import com.cheese.shiro.server.gateway.handler.assign.ZuulAssignRouteHandler;
import com.cheese.shiro.server.gateway.host.CacheHostManager;
import com.cheese.shiro.server.gateway.host.RedisCacheHostManager;
import com.cheese.shiro.server.gateway.register.ServerRegisterManager;
import com.cheese.shiro.server.gateway.uri.ServerUriManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;


/**
 * zuul网关主机路由处理器配置
 * @author sobann
 */
@Configuration
public class ZuulAssignHandlerConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ZuulAssignHandlerConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public CacheHostManager cacheHostManager(RedisConnectionFactory redisConnectionFactory){
        logger.info("prepare to initialize default RedisCacheHostManager");
        return new RedisCacheHostManager(redisConnectionFactory,1800);
    }


    @Bean
    @ConditionalOnMissingBean
    public AssignRouter assignRouter(CacheHostManager cacheHostManager,
                                     ServerRegisterManager serverRegisterManager,
                                     DiscoveryClient discoveryClient){
        AssignRouter assignRouter = new AssignRouter();
        assignRouter.setUriManager(new ServerUriManager<>(serverRegisterManager, AssignRoute.REGISTER_KEY));
        assignRouter.setCacheHostManager(cacheHostManager);
        assignRouter.setDiscoveryClient(discoveryClient);
        logger.info("prepare to initialize default AssignRouter");
        return assignRouter;
    }


    @Bean
    @ConditionalOnMissingBean
    public ZuulAssignRouteHandler zuulAssignRouteHandler(AssignRouter assignRouter){
        ZuulAssignRouteHandler routeHandler = new ZuulAssignRouteHandler();
        routeHandler.setAssignRouter(assignRouter);
        logger.info("prepare to initialize default ZuulAssignRouteHandler");
        return routeHandler;
    }

}
