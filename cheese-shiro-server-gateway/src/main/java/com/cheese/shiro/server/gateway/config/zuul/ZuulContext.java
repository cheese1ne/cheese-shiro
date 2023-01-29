package com.cheese.shiro.server.gateway.config.zuul;

import com.cheese.shiro.common.Context;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.util.StreamUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;

/**
 * zuul 网关的 context 实现类
 * 对 requestContext上下文 进行包装
 * @author sobann
 */
public class ZuulContext implements Context<RequestContext> {

    public static final String REQUEST_BODY ="RequestBody";

    private RequestContext requestContext;

    private String serviceId;

    public ZuulContext(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    public String getServiceId() {
        if(StringUtils.isBlank(this.serviceId)){
            //对于zuul,首先获取"serviceId",即注册中心名称
            Object serviceId = requestContext.get(FilterConstants.SERVICE_ID_KEY);
            if(serviceId !=null){
                this.serviceId = (String) serviceId;
            }else{
                //对于url配置的端口转发，获取proxy参数,即路由名称
                Object proxy = requestContext.get(FilterConstants.PROXY_KEY);
                if(proxy!=null){
                    this.serviceId = (String)proxy;
                }
            }

        }
        return this.serviceId;
    }

    @Override
    public String getRealIp() {
        Object o = getAttribute(REAL_IP);
        if(o != null){
            return (String)o;
        }
        HttpServletRequest request = requestContext.getRequest();
        String ipAddress = getIpAddress(request);
        setAttribute(REAL_IP,ipAddress);
        return ipAddress;
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("x-forwarded-for");

        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknow".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();

        }

        //对于通过多个代理的情况，第一个IP为客户端真实的IP地址，多个IP按照','分割
        if(null != ipAddress && ipAddress.length() > 15){
            //"***.***.***.***".length() = 15
            if(ipAddress.indexOf(",") > 0){
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        if(StringUtils.isNotBlank(ipAddress)){
            if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
                //根据网卡获取本机配置的IP地址
                InetAddress inetAddress = null;
                try {
                    inetAddress = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inetAddress.getHostAddress();
            }
        }
        return ipAddress;
    }

    @Override
    public String getRequestUri() {
        return  (String)requestContext.get(FilterConstants.REQUEST_URI_KEY);
    }

    @Override
    public String getBestUriPattern() {
        Object o = getAttribute(BEST_URI_PATTERN);
        return o==null? null :(String) o;
    }

    @Override
    public void setBestUriPattern(String bestUriPattern) {
        if(bestUriPattern != null){
            setAttribute(BEST_URI_PATTERN,bestUriPattern);
        }
    }

    @Override
    public String getRequestMethod() {
        return requestContext.getRequest().getMethod();
    }

    @Override
    public String getRequestBody() {
        //直接存储在context中，防止多次读取
        String content = null;
        Object requestBody = requestContext.get(REQUEST_BODY);
        if(requestBody==null || StringUtils.isBlank((String) requestBody)){
            try {
                InputStream inputStream = requestContext.getRequest().getInputStream();
                content = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                requestContext.set(REQUEST_BODY,content);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            content = (String)requestBody;
        }
        return content;
    }

    @Override
    public String getRequestContentType() {
        return requestContext.getRequest().getContentType();
    }

    @Override
    public String getRequestParam(String name) {
        return  requestContext.getRequest().getParameter(name);
    }

    @Override
    public String getRequestHeader(String name) {
        return requestContext.getRequest().getHeader(name);
    }

    @Override
    public String getRequestCookie(String name) {
        Cookie[] cookies = requestContext.getRequest().getCookies();
        if(cookies!=null && cookies.length>0){
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if(cookieName.equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public String getErrorContent() {
        Object content = getAttribute(ERROR_CONTENT);
        return content==null ? null:(String)content;
    }

    @Override
    public void setErrorContent(String content) {
        setAttribute(ERROR_CONTENT,content);
    }

    @Override
    public String getErrorContentType() {
        Object type = getAttribute(ERROR_CONTENT_TYPE);
        return type == null ? null :(String) type;
    }

    @Override
    public void setErrorContentType(String type) {
        setAttribute(ERROR_CONTENT_TYPE,type);
    }

    @Override
    public Object getIdentityContext() {
        return requestContext.get(IDENTITY_CONTEXT);
    }

    @Override
    public void setIdentityContext(Object context) {
        requestContext.set(IDENTITY_CONTEXT, context);
    }


    @Override
    public void addRequestHeader(String name, String value) {
        requestContext.addZuulRequestHeader(name,value);
    }

    @Override
    public void addResponseHeader(String name, String value) {
        requestContext.addZuulResponseHeader(name,value);
    }

    @Override
    public void addCookie(String name, String value,String domian,String path) {
        HttpServletResponse response = requestContext.getResponse();
        Cookie cookie = new Cookie(name,value);
        cookie.setPath(path);
        cookie.setDomain(domian);
        response.addCookie(cookie);
    }

    @Override
    public RequestContext getOriginalContext() {
        return requestContext;
    }

    @Override
    public Object getAttribute(String key) {
        return requestContext.get(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        if(key !=null && value !=null){
            requestContext.put(key,value);
        }
    }
}
