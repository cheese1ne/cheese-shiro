package com.cheese.shiro.server.core.expore.jersey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * HTTP的服务注册管理器配置类(Jersey)
 *
 * @author sobann
 */
@Configuration
public class JerseyResourceAutoConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(JerseyResourceAutoConfiguration.class);

    @Bean
    public JerseyConfig jerseyConfig() {
        logger.info("prepare to initialize JerseyServerResource, expose the Jersey service");
        return new JerseyConfig();
    }
}
