package com.cheese.shiro.server.gateway;

import com.cheese.shiro.server.gateway.common.enums.GatewayEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.*;

/**
 * 网关类型自动装配
 *
 * @author sobann
 */
public class GatewayServerAutoImportSelector implements ImportSelector, EnvironmentAware {

    private static final Logger logger = LoggerFactory.getLogger(GatewayServerAutoImportSelector.class);

    private static final String SESSION_CONFIG = "com.cheese.shiro.server.gateway.SessionHandlerAutoConfiguration";
    private static final String DEFAULT_GATEWAY_TYPE = "zuul";


    private static final String GATEWAY_TYPE = "cheese.shiro.gateway.type";
    private static final String SESSION = "cheese.shiro.gateway.session";
    private static final String LOG = "cheese.shiro.gateway.log";
    private static final String ASSIGN = "cheese.shiro.gateway.assign";


    private Environment environment;


    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        String type = environment.getProperty(GATEWAY_TYPE, DEFAULT_GATEWAY_TYPE);
        List<GatewayEnum> gatewayEnums = GatewayEnum.parseEnumList(type);
        if (gatewayEnums.isEmpty()) {
            logger.error("gain a wrong gateway type :{}, now load according to zuulConfig ", type);
            throw new IllegalArgumentException("错误的网关类型配置:" + type);
        }
        String logConfig = GatewayEnum.parse(type, "log").getFullClassName();
        String assignConfig = GatewayEnum.parse(type, "assign").getFullClassName();
        String filterConfig = GatewayEnum.parse(type, "filter").getFullClassName();

        List<String> configurations = new ArrayList<>();
        configurations.add(filterConfig);
        //session
        if (environment.getProperty(SESSION, Boolean.class, true)) {
            configurations.add(SESSION_CONFIG);
        }
        //log
        if (environment.getProperty(LOG, Boolean.class, true)) {
            configurations.add(logConfig);
        }
        //assign
        if (environment.getProperty(ASSIGN, Boolean.class, true)) {
            configurations.add(assignConfig);
        }
        return configurations.toArray(new String[0]);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
