package com.cheese.shiro.server.gateway.handler;


import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.Order;

/**
 * 网关对于单次请求的处理器链抽象父类
 *
 * @author sobann
 */
public abstract class Handler implements Order {

    protected boolean enable = true;

    public boolean isEnable() {
        return enable;
    }


    public void setEnable(boolean enbale) {
        this.enable = enbale;
    }

    /**
     * preHandle为false 时，获取错误响应值
     *
     * @param context
     * @return
     */
    public String getErrorContent(Context context) {
        return context.getErrorContent();
    }

    /**
     * preHandle = false 时，获取响应值类型
     *
     * @param context
     * @return
     */
    public String getErrorContentType(Context context) {
        return context.getErrorContentType();
    }

    /**
     * 路由前处理，根据返回值类型判断是否继续转发
     *
     * @param context
     * @return
     */
    public abstract boolean preHandle(Context context);

    /**
     * 前置条件 preHandle返回true
     * 路由后处理
     *
     * @param context
     */
    public void postHandle(Context context) {
    }

    /**
     * 路由错误时调用
     * @param context
     */
    public void onError(Context context) {
    }

}
