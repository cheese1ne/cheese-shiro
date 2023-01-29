package com.cheese.shiro.server.core.expore.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP的服务注册管理器配置类(Spring MVC)
 *
 * @author sobann
 */
@Configuration
public class SpringMvcResourceAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(SpringMvcResourceAutoConfiguration.class);

    @Bean
    public MvcResourceController springMVCController() {
        logger.info("prepare to initialize MvcResourceController, expose the mvc service");
        return new MvcResourceController();
    }
}
