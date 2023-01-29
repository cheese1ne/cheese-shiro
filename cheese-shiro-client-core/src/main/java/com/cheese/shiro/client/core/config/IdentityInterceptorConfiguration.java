package com.cheese.shiro.client.core.config;

import com.cheese.shiro.client.core.interceptor.MvcIdentityInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 配置 identityTrace加载
 *
 * @author sobann
 */
public class IdentityInterceptorConfiguration implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new MvcIdentityInterceptor()).addPathPatterns("/**");
    }
}
