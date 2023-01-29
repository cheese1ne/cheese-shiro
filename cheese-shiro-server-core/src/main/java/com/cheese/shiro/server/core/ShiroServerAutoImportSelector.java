package com.cheese.shiro.server.core;

import com.cheese.shiro.server.core.common.enums.ExposerEnum;
import com.cheese.shiro.server.core.common.enums.SelectorEnum;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * 权限服务自动装配类
 * @author sobann
 */
public class ShiroServerAutoImportSelector implements ImportSelector, EnvironmentAware {

    public static final String SELECTOR = "cheese.shiro.server.selector";
    public static final String EXPOSER = "cheese.shiro.server.exposer";

    private  Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment =environment;
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        List<String> configurations = new ArrayList<>();
        //获取数据转换器选择器配置
        String selector = environment.getProperty(SELECTOR,"default");
        String selectorConfig = SelectorEnum.parseType(selector);
        configurations.add(selectorConfig);
        //获取鉴权服务暴露方式配置
        String exposer = environment.getProperty(EXPOSER,"jersey");
        String exposerConfig = ExposerEnum.parseType(exposer);
        configurations.add(exposerConfig);
        return configurations.toArray(new String[0]);
    }
}
