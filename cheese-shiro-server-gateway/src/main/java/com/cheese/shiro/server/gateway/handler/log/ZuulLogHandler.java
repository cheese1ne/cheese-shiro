package com.cheese.shiro.server.gateway.handler.log;

import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.domain.GatewayLogContent;
import com.cheese.shiro.common.manager.identity.IdentityManager;
import com.cheese.shiro.common.manager.uri.entity.GatewayLogInfo;
import com.cheese.shiro.common.manager.uri.entity.GatewayLogUriMapping;
import com.cheese.shiro.common.util.DateUtils;
import com.cheese.shiro.common.util.ParamUtils;
import com.cheese.shiro.server.gateway.config.zuul.ZuulContext;
import com.cheese.shiro.server.gateway.handler.auth.AuthHandler;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.ribbon.apache.RibbonApacheHttpResponse;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.*;

/**
 * @GatewayLog 实现
 * order:50 第一个
 * pre:开始时间
 * post: 记录返回值
 */
public class ZuulLogHandler extends AbstractLogHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZuulLogHandler.class);
    @Override
    public void postHandle(Context requestContext) {
        try {
            ZuulContext context= (ZuulContext) requestContext;
            RequestContext currentContext = context.getOriginalContext();
            GatewayLogUriMapping matchUriMapping = uriManager.getMatchUriMapping(context);
            if(matchUriMapping==null){
                return;
            }
            GatewayLogInfo gatewayLogInfo = matchUriMapping.getGatewayLogInfo();
            GatewayLogContent content = createPreLog(context, gatewayLogInfo);
            //响应码
            int responseStatusCode = currentContext.getResponseStatusCode();
            content.setResponseStatus(responseStatusCode);
            //路由地址
            URL routeHost = currentContext.getRouteHost();
            if(routeHost !=null){
                content.setRouteNode(routeHost.getAuthority());
            }else {
                Object o = currentContext.get("ribbonResponse");
                if(o!=null && o instanceof RibbonApacheHttpResponse){
                    URI requestedURI = ((RibbonApacheHttpResponse) o).getRequestedURI();
                    content.setRouteNode(requestedURI.getAuthority());
                }
            }

            //响应内容
            if(gatewayLogInfo.isResponseBody()){
                InputStream responseDataStream = currentContext.getResponseDataStream();
                try {
                    if(responseDataStream!=null){
                        String responseBody = IOUtils.toString(responseDataStream);
                        content.setResponseBody(responseBody);
                        currentContext.setResponseBody(responseBody);
                    }
                } catch (IOException e) {
                    logger.error("Copy Response Body Error",e);
                }
            }
            //异常信息
            Throwable throwable = currentContext.getThrowable();
            if(throwable!=null){
                content.setException(throwable.getMessage());
            }
            logWriter.write(content);
        } catch (Exception e) {
            logger.error("Write GateWay Log Error",e);
        }
    }

    private GatewayLogContent createPreLog(ZuulContext zuulContext, GatewayLogInfo logInfo){
        boolean requestBody = logInfo.isRequestBody();
        GatewayLogContent content = new GatewayLogContent(zuulContext,logInfo);
        content.setGatewayNode(gatewayNode);
        content.setEndTime(DateUtils.getNow());
        Object startTime = zuulContext.getOriginalContext().get(START_TIME);
        if(startTime !=null){
            content.setStartTime(DateUtils.format((Date) startTime));
        }
        Object identityContext = zuulContext.getIdentityContext();
        if(identityContext!=null){
            content.setUser(IdentityManager.getContextParser().getIdentity(identityContext));
        }
        //获取requestbody
        String requestContentType = zuulContext.getRequestContentType();
        if(requestBody && !content.getHttpMethod().equalsIgnoreCase("get") && StringUtils.isNotEmpty(requestContentType)){
            //json
            if(requestContentType.contains(MediaType.APPLICATION_JSON_VALUE)){
                content.setRequestBody(zuulContext.getRequestBody());
            }else if(requestContentType.contains(MediaType.APPLICATION_FORM_URLENCODED_VALUE)){
                //form
                Enumeration<String> parameterNames = zuulContext.getOriginalContext().getRequest().getParameterNames();
                Map<String,String> body = new HashMap<>();
                while (parameterNames.hasMoreElements()){
                    String key = parameterNames.nextElement();
                    body.put(key,zuulContext.getRequestParam(key));
                }
                content.setRequestBody(body.toString());
            }
        }
        //获取instanceId
        String instanceIdExpress = logInfo.getInstanceId();
        if(StringUtils.isNotBlank(instanceIdExpress)){
            Set<String> instanceIds = ParamUtils.getParameterFromRequestAndBody(zuulContext, instanceIdExpress);
            if(!CollectionUtils.isEmpty(instanceIds)){
                content.setInstanceIds(new ArrayList<>(instanceIds));
            }
        }
        //存入当前鉴权配置
        Object attribute = zuulContext.getAttribute(AuthHandler.PERM);
        if(attribute!=null){
            content.setAuth((String) attribute);
        }
        return content;
    }
}
