package com.cheese.shiro.client.core.register.http;

import com.cheese.shiro.client.core.checker.register.HttpServiceRegisterStatusChecker;
import com.cheese.shiro.client.core.checker.register.ServiceRegisterStatusChecker;
import com.cheese.shiro.client.core.props.RegisterProp;
import com.cheese.shiro.client.core.register.ClientRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 在eureka服务注册完成之后加载实例
 * 解决负载均衡实例无法解析网关服务实例ip:port的问题
 *
 * @author sobann
 */
@Configuration
public class HttpClientRegisterConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientRegisterConfiguration.class);

    @Value("${spring.cloud.client.ip-address}")
    private String ip;
    @Value("${server.port}")
    private String port;

    /**
     * 服务调用时默认RestTemplate
     *
     * @return
     */
    @LoadBalanced
    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        logger.info("prepare to initialize RestTemplate");
        return new RestTemplate();
    }

    /**
     * 默认创建HttpClientConfigManager
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ServiceRegisterStatusChecker clientRegisterChecker(DiscoveryClient discoveryClient,
                                                              RegisterProp registerProp) {
        String host = ip + ":" + port;
        HttpServiceRegisterStatusChecker clientRegisterChecker = new HttpServiceRegisterStatusChecker(discoveryClient, registerProp, host);
        logger.info("prepare to initialize HttpServiceRegisterStatusChecker");
        return clientRegisterChecker;
    }

    /**
     * 默认创建HttpClientConfigManager
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public ClientRegister clientRegister(RestTemplate restTemplate,
                                         RegisterProp registerProp,
                                         ServiceRegisterStatusChecker serviceRegisterStatusChecker) {
        HttpClientRegister clientRegister = new HttpClientRegister(serviceRegisterStatusChecker, restTemplate, registerProp.getServer(), registerProp.getThreadNum(), registerProp.getInterval(), registerProp.getSyncInterval(), registerProp.isServerDynamic());
        clientRegister.setService(registerProp.getService());
        logger.info("prepare to initialize HttpClientRegister");
        return clientRegister;
    }
}
