package com.cheese.shiro.rpc.dubbo;

import com.cheese.shiro.common.service.ShiroServiceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rpc调用方式为dubbo的spring自动装配类
 * @author sobann
 */
@Configuration
public class ShiroDubboAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ShiroDubboAutoConfiguration.class);
    @Bean
    public ShiroServiceProvider shiroServiceProvider(){
        logger.info("prepare to initialize DubboShiroServiceProvider");
        return new DubboShiroServiceProvider();
    }
}
