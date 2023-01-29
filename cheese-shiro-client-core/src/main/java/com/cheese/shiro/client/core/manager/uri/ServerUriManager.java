package com.cheese.shiro.client.core.manager.uri;

import com.cheese.shiro.client.core.register.UriConfigRegister;
import com.cheese.shiro.common.anno.ServerUri;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 从requestMappingHandlerMapping中获取添加@ServiceUri的处理器信息(路径、处理器字节码和方法对象等)
 *
 * @author sobann
 */
public class ServerUriManager extends MvcUriManager<UriMapping> implements UriConfigRegister<UriMapping> {

    @Override
    public boolean isUriMapping(Method method, Class clazz) {
        return method.isAnnotationPresent(ServerUri.class);
    }

    @Override
    public UriMapping getUriMapping(RequestMappingInfo mappingInfo, Method method, Class clazz) {
        return getUriMapping(mappingInfo);
    }

    @Override
    public List<UriMapping> getUriMappingsForRegister() {
        return getUriMapping();
    }

    @Override
    public List<UriMapping> getUriPatternsForRegister() {
        return getUriPatterns();
    }

    @Override
    public String getUriRegisterKey() {
        return ServerUri.REGISTER_KEY;
    }
}
