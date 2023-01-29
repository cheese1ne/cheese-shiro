package com.cheese.shiro.client.core.manager.uri;

import com.cheese.shiro.client.core.register.UriConfigRegister;
import com.cheese.shiro.common.anno.GatewayLog;
import com.cheese.shiro.common.manager.uri.entity.GatewayLogInfo;
import com.cheese.shiro.common.manager.uri.entity.GatewayLogUriMapping;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 从requestMappingHandlerMapping中获取添加@GatewayLog的处理器信息(路径、处理器字节码和方法对象等)
 *
 * @author sobann
 */
public class GatewayLogUriManager extends MvcUriManager<GatewayLogUriMapping> implements UriConfigRegister<GatewayLogUriMapping> {
    @Override
    public boolean isUriMapping(Method method, Class clazz) {
        return method.isAnnotationPresent(GatewayLog.class) || clazz.isAnnotationPresent(GatewayLog.class);
    }

    @Override
    public GatewayLogUriMapping getUriMapping(RequestMappingInfo mappingInfo, Method method, Class<?> clazz) {
        UriMapping uriMapping = getUriMapping(mappingInfo);
        GatewayLogInfo logInfo = null;
        if (clazz.isAnnotationPresent(GatewayLog.class)) {
            GatewayLog clazzAnnotation = clazz.getAnnotation(GatewayLog.class);
            logInfo = new GatewayLogInfo(clazzAnnotation);
            if (method.isAnnotationPresent(GatewayLog.class)) {
                GatewayLog methodAnnotation = method.getAnnotation(GatewayLog.class);
                logInfo.reset(methodAnnotation);
            }
        } else {
            GatewayLog gatewayLog = method.getAnnotation(GatewayLog.class);
            logInfo = new GatewayLogInfo(gatewayLog);
        }
        logInfo.setClazz(clazz.getCanonicalName());
        logInfo.setMethod(method.getName());
        GatewayLogUriMapping gatewayLogUriMapping = new GatewayLogUriMapping(logInfo);
        gatewayLogUriMapping.addUriMapping(uriMapping);
        return gatewayLogUriMapping;
    }

    @Override
    public List<GatewayLogUriMapping> getUriMappingsForRegister() {
        return getUriMapping();
    }

    @Override
    public List<UriMapping> getUriPatternsForRegister() {
        return getUriPatterns();
    }

    @Override
    public String getUriRegisterKey() {
        return GatewayLog.REGISTER_KEY;
    }
}
