package com.cheese.shiro.client.core.interceptor;

import com.cheese.shiro.common.manager.identity.IdentityManager;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * SpringMVC中用户身份信息传递
 * 拦截器通过HandlerExecutionChain链完成调用
 *
 * @author sobann
 */
public class MvcIdentityInterceptor implements HandlerInterceptor {

    /**
     * 线程中存储当前用户身份信息
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String identityName = IdentityManager.getContextTracerName();
        String identity = request.getHeader(identityName);
        IdentityManager.bind(identity);
        return true;
    }

    /**
     * preHandle返回false时执行
     * 线程解绑身份信息
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (IdentityManager.hasContext()) {
            IdentityManager.unBind();
        }
    }
}
