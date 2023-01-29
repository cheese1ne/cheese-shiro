package com.cheese.shiro.server.gateway.config.zuul.filter;

import com.cheese.shiro.server.gateway.config.zuul.ZuulContext;
import com.cheese.shiro.server.gateway.handler.Handler;
import com.cheese.shiro.server.gateway.handler.HandlerManager;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * 网关错误过滤器
 * 在上下文中抛出错误时符合过滤条件，执行run方法
 * 调用处理器中的onError方法进行处理
 * @author sobann
 */
public class ShiroZuulErrorFilter extends ZuulFilter {
    @Lazy
    @Autowired
    protected HandlerManager handlerManager;

    @Override
    public String filterType() {
        return FilterConstants.ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.SEND_ERROR_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getThrowable()!=null;
    }

    @Override
    public Object run() throws ZuulException {
        List<Handler> handlers = handlerManager.getHandlers();
        RequestContext currentContext = RequestContext.getCurrentContext();
        ZuulContext context= new ZuulContext(currentContext);
        for (int i = handlers.size()-1; i >=0 ; i--) {
            Handler handler = handlers.get(i);
            if(handler.isEnable()){
                handler.onError(context);
            }
        }
        return null;
    }
}
