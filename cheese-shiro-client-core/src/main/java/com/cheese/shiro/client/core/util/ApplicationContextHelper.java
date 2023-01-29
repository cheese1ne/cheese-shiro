package com.cheese.shiro.client.core.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * springIOC容器工具
 *
 * @author sobann
 */
public class ApplicationContextHelper implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    public <T> T getBean(Class<T> clazz) {
        return (T) applicationContext.getBean(clazz);
    }

    public Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public <T> Map<String, T> getBeans(Class<T> clazz) {
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(applicationContext, clazz, true, false);
    }
}
