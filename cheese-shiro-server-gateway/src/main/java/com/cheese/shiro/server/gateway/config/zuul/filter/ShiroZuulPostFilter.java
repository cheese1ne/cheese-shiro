package com.cheese.shiro.server.gateway.config.zuul.filter;

import com.cheese.shiro.server.gateway.config.zuul.ZuulContext;
import com.cheese.shiro.server.gateway.handler.Handler;
import com.cheese.shiro.server.gateway.handler.HandlerManager;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

/**
 * 网关后置过滤器
 * 默认全部过滤
 * 调用处理器中的postHandle方法进行处理
 * @author sobann
 */
public class ShiroZuulPostFilter extends ZuulFilter {

    @Lazy
    @Autowired
    protected HandlerManager handlerManager;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 10;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        List<Handler> handlers = handlerManager.getHandlers();
        RequestContext currentContext = RequestContext.getCurrentContext();
        ZuulContext context= new ZuulContext(currentContext);
        for (int i = handlers.size()-1; i >=0 ; i--) {
            Handler handler = handlers.get(i);
            if(handler.isEnable()){
                handler.postHandle(context);
            }
        }
        return null;
    }
}
