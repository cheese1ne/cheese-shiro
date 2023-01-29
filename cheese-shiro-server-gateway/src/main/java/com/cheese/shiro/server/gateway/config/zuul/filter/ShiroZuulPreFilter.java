package com.cheese.shiro.server.gateway.config.zuul.filter;

import com.cheese.shiro.server.gateway.config.zuul.ZuulContext;
import com.cheese.shiro.server.gateway.handler.Handler;
import com.cheese.shiro.server.gateway.handler.HandlerManager;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 网关前置过滤器
 * 默认全部过滤
 * 调用处理器中的preHandle方法进行预处理，并根据处理结果判断是否进行返回
 *
 * @author sobann
 */
public class ShiroZuulPreFilter extends ZuulFilter {

    @Lazy
    @Autowired
    protected HandlerManager handlerManager;

    @Override
    public String filterType() {
        return "route";
    }

    @Override
    public int filterOrder() {
        return 3;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }


    @Override
    public Object run() throws ZuulException {
        RequestContext currentContext = RequestContext.getCurrentContext();
        ZuulContext context = new ZuulContext(currentContext);
        List<Handler> handlers = handlerManager.getHandlers();
        for (Handler handler : handlers) {
            //handler，且校验未通过时
            if (handler.isEnable() && !handler.preHandle(context)) {
                String errorContent = handler.getErrorContent(context);
                String errorContentType = handler.getErrorContentType(context);
                //设置相应内容，直接返回
                refuse(context.getOriginalContext(), errorContent, errorContentType);
                return null;
            }
        }
        return null;
    }

    /**
     * 服务器响应错误内容
     *
     * @param currentContext
     * @param content
     * @param contentType
     */
    public void refuse(RequestContext currentContext, String content, String contentType) {
        currentContext.setSendZuulResponse(false);
        HttpServletResponse response = currentContext.getResponse();
        response.setContentType(contentType);
        currentContext.setResponse(response);
        currentContext.setResponseBody(content);
    }
}
