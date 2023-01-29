package com.cheese.shiro.rpc.feign.interceptor;

import com.cheese.shiro.common.manager.identity.IdentityManager;
import feign.RequestInterceptor;
import feign.RequestTemplate;

/**
 * feign服务间调用用户身份信息传递
 * @author sobann
 */
public class ShiroFeignTracerInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        if(IdentityManager.hasContext()){
            template.header(IdentityManager.getContextTracerName(),IdentityManager.getContext());
        }
    }
}
