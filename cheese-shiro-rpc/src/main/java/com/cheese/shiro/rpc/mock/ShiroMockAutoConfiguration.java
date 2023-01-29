package com.cheese.shiro.rpc.mock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 屏蔽权限rpc调用，测试时使用
 * @author sobann
 */
@Configuration
public class ShiroMockAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ShiroMockAutoConfiguration.class);
    @Bean
    public MockShiroServiceProvider mockShiroServiceProvider(){
        logger.info("prepare to initialize MockShiroServiceProvider");
        return new MockShiroServiceProvider();
    }
}
