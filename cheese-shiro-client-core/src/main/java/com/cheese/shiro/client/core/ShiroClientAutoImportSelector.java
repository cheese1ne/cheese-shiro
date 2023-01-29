package com.cheese.shiro.client.core;

import com.cheese.shiro.client.core.common.enums.RegisterTypeEnum;
import com.cheese.shiro.common.manager.identity.IdentityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 客户端 注册方式 rpc远程调用方式 配置
 *
 * @author sobann
 */
public class ShiroClientAutoImportSelector implements ImportSelector, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(ShiroClientAutoImportSelector.class);
    public static final String CHEESE_SHIRO_MOCK_ENABLE = "cheese.shiro.mock.enable";
    public static final String CHEESE_SHIRO_MOCK_NAME = "cheese.shiro.mock.name";
    public static final String CHEESE_SHIRO_MOCK_DEFAULTVALUE = "cheese.shiro.mock.defaultValue";
    public static final String CHEESE_SHIRO_MOCK_CONTEXT = "cheese.shiro.mock.defaultValue";

    private Environment environment;

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Boolean mock = environment.getProperty(CHEESE_SHIRO_MOCK_ENABLE, Boolean.class, false);
        //Mock时不进行客户端注册
        if (mock) {
            String tracer = environment.getProperty(CHEESE_SHIRO_MOCK_NAME, "context_tracer");
            String defaultValue = environment.getProperty(CHEESE_SHIRO_MOCK_DEFAULTVALUE, "-1");
            String context = environment.getProperty(CHEESE_SHIRO_MOCK_CONTEXT);
            IdentityManager.configure(tracer, defaultValue);
            IdentityManager.mock(true, context);
            logger.info("shiro.mock.enable is true, the registration function is disabled");
            return new String[0];
        }
        String registerType = environment.getProperty("cheese.shiro.register.type", "http");
        String registerConfig = RegisterTypeEnum.parse(registerType);
        logger.info("Auto Configuration For Shiro Client Register :{}", registerType);
        return new String[]{registerConfig};
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
}
