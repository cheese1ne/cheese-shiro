package com.cheese.shiro.server.core.expore.dubbo;

import com.cheese.shiro.common.service.ShiroService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * dubbo权限资源配置
 *
 * @author sobann
 */
@Configuration
public class DubboResourceAutoConfiguration {
    @Bean
    public DubboShiroService dubboShiroService(ShiroService shiroService) {
        DubboShiroService dubboShiroService = new DubboShiroService();
        dubboShiroService.setShiroService(shiroService);
        return dubboShiroService;
    }
}
