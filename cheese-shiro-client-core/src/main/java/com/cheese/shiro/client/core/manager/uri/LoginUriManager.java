package com.cheese.shiro.client.core.manager.uri;

import com.cheese.shiro.client.core.register.UriConfigRegister;
import com.cheese.shiro.common.anno.Auth;
import com.cheese.shiro.common.anno.Login;
import com.cheese.shiro.common.anno.MultipleAuth;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 从requestMappingHandlerMapping中获取添加@Login的处理器信息(路径、处理器字节码和方法对象等)
 * 如果@Auth(login=true) 该Url也会被收集
 *
 * @author sobann
 */
public class LoginUriManager extends MvcUriManager<UriMapping> implements UriConfigRegister<UriMapping> {

    @Override
    public boolean isUriMapping(Method method, Class clazz) {
        return methodIsNeedLogin(method, clazz);
    }

    @Override
    public UriMapping getUriMapping(RequestMappingInfo mappingInfo, Method method, Class clazz) {
        return getUriMapping(mappingInfo);
    }

    public static boolean methodIsNeedLogin(Method method, Class clazz) {

        //检查类上是否带有@Login,如果有，则直接返回true
        if (clazz.isAnnotationPresent(Login.class)) {
            return true;
        }
        //检查 @Auth
        if (method.isAnnotationPresent(Auth.class)) {
            Auth annotation = method.getAnnotation(Auth.class);
            if (annotation.login()) {
                //如果存在前置，则记录，并且之间下一个,不在检查@Login
                return true;
            }
        }
        //检查 @MultipleAuth
        if (method.isAnnotationPresent(MultipleAuth.class)) {
            MultipleAuth multipleAuth = method.getAnnotation(MultipleAuth.class);
            Auth[] value = multipleAuth.value();
            if (value != null && value.length > 0) {
                for (Auth auth : value) {
                    if (auth.login()) {
                        return true;
                    }
                }
            }
        }
        //后检查是否存在@Login
        return method.isAnnotationPresent(Login.class);
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
        return Login.REGISTER_KEY;
    }
}
