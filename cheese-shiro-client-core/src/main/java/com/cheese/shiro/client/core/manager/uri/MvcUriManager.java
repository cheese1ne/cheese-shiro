package com.cheese.shiro.client.core.manager.uri;


import com.cheese.shiro.client.core.util.ApplicationContextHelper;
import com.cheese.shiro.common.manager.uri.UriManager;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 获取Controller 中所有 @Auth/@Login/@ServerUri 中的uri 映射信息
 * 针对SpringMvc框架
 * webflux框架无法使用
 *
 * @author sobann
 */
public abstract class MvcUriManager<E> extends UriManager {

    protected ApplicationContextHelper applicationContextHelper;

    private List<UriMapping> uriPatterns = new ArrayList<>();

    private List<E> uriMappings = new ArrayList<>();

    public void setApplicationContextHelper(ApplicationContextHelper applicationContextHelper) {
        this.applicationContextHelper = applicationContextHelper;
    }


    /**
     * 判断该方法是否收集UriMapping信息 @RequestMapping
     *
     * @param method
     * @param clazz
     * @return
     */
    public abstract boolean isUriMapping(Method method, Class clazz);

    /**
     * 提取@RequestMapping信息 至UriMapping
     *
     * @param mappingInfo
     * @param method
     * @return
     */
    public abstract E getUriMapping(RequestMappingInfo mappingInfo, Method method, Class<?> clazz);


    /**
     * 获取当前 自身 服务中 isUriMapping=true 的Uri@RequestMapping信息
     *
     * @return
     */
    public void getUriMappingsAndUriPatterns() {
        List<UriMapping> appPatterns = new ArrayList<>();
        List<E> appMappings = new ArrayList<>();
        Map<String, HandlerMapping> allRequestMappings = applicationContextHelper.getBeans(HandlerMapping.class);
        for (HandlerMapping handlerMapping : allRequestMappings.values()) {
            if (handlerMapping instanceof RequestMappingHandlerMapping) {
                RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) handlerMapping;
                Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
                for (Map.Entry<RequestMappingInfo, HandlerMethod> requestMappingInfo : handlerMethods.entrySet()) {
                    RequestMappingInfo mappingInfo = requestMappingInfo.getKey();
                    HandlerMethod handlerMethod = requestMappingInfo.getValue();
                    UriMapping mapping = getUriMapping(mappingInfo);
                    appPatterns.add(mapping);
                    Method method = handlerMethod.getMethod();
                    Class<?> clazz = handlerMethod.getBeanType();
                    if (isUriMapping(method, clazz)) {
                        E uriMapping = getUriMapping(mappingInfo, method, clazz);
                        if (uriMapping != null) {
                            appMappings.add(uriMapping);
                        }
                    }
                }
            }
        }
        uriMappings = appMappings;
        uriPatterns = appPatterns;
    }

    public UriMapping getUriMapping(RequestMappingInfo mappingInfo) {
        RequestMethodsRequestCondition methodsCondition = mappingInfo.getMethodsCondition();
        Set<RequestMethod> methods = methodsCondition.getMethods();
        Set<String> patterns = mappingInfo.getPatternsCondition().getPatterns();
        UriMapping uriMapping = new UriMapping();
        //映射至所有Url
        uriMapping.setPatterns(patterns);
        Set<String> requestMethods = new HashSet<>();
        if (!CollectionUtils.isEmpty(methods)) {
            for (RequestMethod request : methods) {
                requestMethods.add(request.name().toLowerCase());
            }
        }
        uriMapping.setRequestMethods(requestMethods);
        return uriMapping;
    }

    public List<UriMapping> getUriPatterns() {
        if (CollectionUtils.isEmpty(uriPatterns)) {
            getUriMappingsAndUriPatterns();
        }
        return uriPatterns;
    }

    public List<E> getUriMapping() {
        if (CollectionUtils.isEmpty(uriMappings)) {
            getUriMappingsAndUriPatterns();
        }
        return uriMappings;
    }

    @Override
    public List<E> getUriMappings(String appName) {
        return getUriMapping();
    }

    @Override
    public List<UriMapping> getUriPatterns(String appName) {
        return getUriPatterns();
    }


}
