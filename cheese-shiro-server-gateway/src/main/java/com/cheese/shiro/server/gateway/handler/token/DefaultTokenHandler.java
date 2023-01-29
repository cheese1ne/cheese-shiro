package com.cheese.shiro.server.gateway.handler.token;


import com.cheese.shiro.common.Context;

/**
 * 默认的token处理器
 * 若需要通过第三方进行认证，通过自实现doWithOtherCredentials注入IOC即可
 * @author sobann
 */
public class DefaultTokenHandler extends TokenHandler {
    @Override
    public boolean doWithOtherCredentials(Context context) {
        return false;
    }

    @Override
    public boolean doWithTokenIsOk(String token, Context context) {
        return true;
    }
}
