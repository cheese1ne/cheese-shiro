package com.cheese.shiro.client.core.manager.uri;


import com.cheese.shiro.client.core.register.UriConfigRegister;
import com.cheese.shiro.common.anno.Auth;
import com.cheese.shiro.common.anno.MultipleAuth;
import com.cheese.shiro.common.manager.uri.entity.AuthInfo;
import com.cheese.shiro.common.manager.uri.entity.AuthUriMapping;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 从requestMappingHandlerMapping中获取添加@Auth或@MultipleAuth的请求处理器信息(路径、处理器字节码和方法对象等)
 *
 * @author sobann
 */
public class AuthUriManager extends MvcUriManager<AuthUriMapping> implements UriConfigRegister<AuthUriMapping> {

    private String defaultApp;

    public void setDefaultApp(String defaultApp) {
        this.defaultApp = defaultApp;
    }

    @Override
    public AuthUriMapping getUriMapping(RequestMappingInfo mappingInfo, Method method, Class<?> clazz) {
        UriMapping uriMapping = getUriMapping(mappingInfo);
        if (method.isAnnotationPresent(Auth.class)) {
            Auth auth = method.getAnnotation(Auth.class);
            //获取auth信息
            AuthInfo authInfo = new AuthInfo(auth);
            if (StringUtils.isBlank(authInfo.getApp())) {
                authInfo.setApp(defaultApp);
            }
            AuthUriMapping authUriMapping = new AuthUriMapping(authInfo);
            authUriMapping.addUriMapping(uriMapping);
            return authUriMapping;
        }
        MultipleAuth multi = method.getAnnotation(MultipleAuth.class);
        Auth[] value = multi.value();
        List<AuthInfo> authInfos = new ArrayList<>();
        for (Auth a : value) {
            AuthInfo authInfo = new AuthInfo(a);
            if (StringUtils.isBlank(authInfo.getApp())) {
                authInfo.setApp(defaultApp);
            }
            authInfos.add(authInfo);
        }
        AuthUriMapping authUriMapping = new AuthUriMapping(authInfos);
        authUriMapping.addUriMapping(uriMapping);
        return authUriMapping;
    }

    @Override
    public boolean isUriMapping(Method method, Class clazz) {
        return method.isAnnotationPresent(Auth.class) || method.isAnnotationPresent(MultipleAuth.class);
    }

    @Override
    public List<AuthUriMapping> getUriMappingsForRegister() {
        return getUriMapping();
    }

    @Override
    public List<UriMapping> getUriPatternsForRegister() {
        return getUriPatterns();
    }

    @Override
    public String getUriRegisterKey() {
        return Auth.REGISTER_KEY;
    }
}
