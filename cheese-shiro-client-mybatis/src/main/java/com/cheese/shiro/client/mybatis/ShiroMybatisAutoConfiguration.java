package com.cheese.shiro.client.mybatis;

import com.cheese.shiro.client.mybatis.intercepter.MybatisInterceptor;
import com.cheese.shiro.client.mybatis.manager.AuthKeyManager;
import com.cheese.shiro.client.mybatis.manager.MybatisAuthKeyManager;
import com.cheese.shiro.client.mybatis.props.MybatisProp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * mybatis 权限配置
 * 通过spring.factories注入
 *
 * @author sobann
 */
@ComponentScan(basePackages = "com.cheese.shiro.client.mybatis.props")
@Configuration
public class ShiroMybatisAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(ShiroMybatisAutoConfiguration.class);

    @Bean
    @ConditionalOnMissingBean
    public AuthKeyManager authKeyManager(MybatisProp mybatisProp) {
        AuthKeyManager manager = new MybatisAuthKeyManager(mybatisProp.getAuthParam());
        logger.info("prepare to initialize MybatisAuthKeyManager");
        return manager;
    }


    @Bean
    @ConditionalOnMissingBean
    public MybatisInterceptor mybatisInterceptor(AuthKeyManager authKeyManager) {
        MybatisInterceptor interceptor = new MybatisInterceptor();
        interceptor.setAuthKeyManager(authKeyManager);
        logger.info("prepare to initialize MybatisInterceptor");
        return interceptor;
    }
}
