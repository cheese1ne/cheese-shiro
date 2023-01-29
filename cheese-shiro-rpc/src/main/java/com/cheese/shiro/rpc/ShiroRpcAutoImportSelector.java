package com.cheese.shiro.rpc;

import com.cheese.shiro.rpc.common.enums.RpcEnum;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

/**
 * rpc调用自动装配
 *
 * @author sobann
 */
public class ShiroRpcAutoImportSelector implements ImportSelector, EnvironmentAware {
    private static final Logger logger = LoggerFactory.getLogger(ShiroRpcAutoImportSelector.class);
    private Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        Boolean mockEnable = environment.getProperty("cheese.shiro.mock.enable", Boolean.class, false);
        if (mockEnable) {
            logger.info("shiro.mock.enable is true, prepare to load ShiroMockAutoConfiguration");
            return new String[]{RpcEnum.MOCK.getFullClassName()};
        }
        String rpcType = environment.getProperty("cheese.shiro.rpc.type", "");
        if(!StringUtils.isBlank(rpcType)){
            String configuration = RpcEnum.parseType(rpcType);
            logger.info("Auto Configuration For CheeseShiroRpc is :{}", rpcType);
            return new String[]{configuration};
        }
        //对于权限提供服务本身需要进行鉴权时，不需要对自身基于feign进行调用
        return new String[0];
    }
}
