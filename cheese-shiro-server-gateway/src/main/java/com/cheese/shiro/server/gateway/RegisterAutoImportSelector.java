package com.cheese.shiro.server.gateway;

import com.cheese.shiro.server.gateway.common.enums.RegisterTypeEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 网关注册器自动装配
 *
 * @author sobann
 */
public class RegisterAutoImportSelector implements ImportSelector, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(RegisterAutoImportSelector.class);
    private Environment environment;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        //默认通过http方式进行信息的注册
        String type = environment.getProperty("cheese.shiro.gateway.register.type", "http");
        String configuration = RegisterTypeEnum.parse(type);
        if (!StringUtils.isBlank(configuration)) {
            logger.info("Auto Configuration For Shiro Gateway Register : {}", type);
            return new String[]{configuration};
        }
        return new String[0];
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
