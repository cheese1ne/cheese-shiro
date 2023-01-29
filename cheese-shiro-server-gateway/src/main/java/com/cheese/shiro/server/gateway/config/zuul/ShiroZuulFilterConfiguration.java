package com.cheese.shiro.server.gateway.config.zuul;

import com.cheese.shiro.server.gateway.config.zuul.filter.ShiroZuulErrorFilter;
import com.cheese.shiro.server.gateway.config.zuul.filter.ShiroZuulPostFilter;
import com.cheese.shiro.server.gateway.config.zuul.filter.ShiroZuulPreFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 默认执行顺序：error(-1) -> pre(3) -> post(10)
 * @author sobann
 */
@Configuration
public class ShiroZuulFilterConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ShiroZuulErrorFilter shiroZuulErrorFilter(){
        return new ShiroZuulErrorFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShiroZuulPostFilter shiroZuulPostFilter(){
        return new ShiroZuulPostFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ShiroZuulPreFilter shiroZuulPreFilter(){
        return new ShiroZuulPreFilter();
    }
}
