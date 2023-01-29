package com.cheese.shiro.server.gateway.handler.identity;


import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.manager.identity.IdentityManager;
import com.cheese.shiro.server.gateway.handler.Handler;

/**
 * 用户身份信息处理器，用于传递传递当前用户身份信息
 * 链条中的最后一个处理器，防止identityContext在处理链中被修改
 * order 500
 * @author sobann
 */
public class IdentityContextHandler extends Handler {
    @Override
    public boolean preHandle(Context context) {
        Object identityContext = context.getIdentityContext();
        if(identityContext!=null){
            try {
                String trace = IdentityManager.getCoder().encode(identityContext);
                context.addRequestHeader(IdentityManager.getContextTracerName(),trace);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public int order() {
        return 500;
    }
}
