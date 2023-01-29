package com.cheese.shiro.client.core.manager.uri;

import com.cheese.shiro.client.core.register.UriConfigRegister;
import com.cheese.shiro.common.anno.AssignRoute;
import com.cheese.shiro.common.manager.uri.entity.AssignRouteMapping;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 网关路由指派的yru路径管理器
 *
 * @author sobann
 */
public class AssignRouteUriManager extends MvcUriManager<AssignRouteMapping> implements UriConfigRegister<AssignRouteMapping> {
    @Override
    public boolean isUriMapping(Method method, Class clazz) {
        return method.isAnnotationPresent(AssignRoute.class);
    }

    @Override
    public AssignRouteMapping getUriMapping(RequestMappingInfo mappingInfo, Method method, Class<?> clazz) {
        UriMapping uriMappings = getUriMapping(mappingInfo);
        AssignRoute annotation = method.getAnnotation(AssignRoute.class);
        return new AssignRouteMapping(uriMappings, annotation);
    }

    @Override
    public List<AssignRouteMapping> getUriMappingsForRegister() {
        return getUriMapping();
    }

    @Override
    public List<UriMapping> getUriPatternsForRegister() {
        return getUriPatterns();
    }

    @Override
    public String getUriRegisterKey() {
        return AssignRoute.REGISTER_KEY;
    }
}
