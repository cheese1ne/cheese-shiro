package com.cheese.shiro.rpc.feign;

import com.cheese.shiro.rpc.feign.interceptor.ShiroFeignTracerInterceptor;
import com.cheese.shiro.rpc.feign.strategy.IdentityHystrixConcurrencyStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * rpc调用方式为feign的spring自动装配类
 * @author sobann
 */
@EnableFeignClients("com.cheese.shiro.rpc.feign")
@Configuration
public class ShiroFeignAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ShiroFeignAutoConfiguration.class);

    @Bean
    public ShiroFeignHystrix shiroFeignHystrix(){
        logger.info("prepare to initialize ShiroFeignHystrix");
        return new ShiroFeignHystrix();
    }

    @Bean
    public FeignShiroServiceProvider feiginShiroServiceProvider(){
        logger.info("prepare to initialize FeignShiroServiceProvider");
        return new FeignShiroServiceProvider();
    }

    @Bean
    public IdentityHystrixConcurrencyStrategy identityHystrixConcurrencyStrategy(){
        logger.info("prepare to initialize IdentityHystrixConcurrencyStrategy");
        return new IdentityHystrixConcurrencyStrategy();
    }

    @Bean
    public ShiroFeignTracerInterceptor shiroFeignTracer(){
        logger.info("prepare to initialize ShiroFeignTracerInterceptor");
        return new ShiroFeignTracerInterceptor();
    }
}
