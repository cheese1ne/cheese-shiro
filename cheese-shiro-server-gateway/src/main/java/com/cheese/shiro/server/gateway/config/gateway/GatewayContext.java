package com.cheese.shiro.server.gateway.config.gateway;

import com.cheese.shiro.common.Context;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * gateway 网关的 context 实现类
 * 对 requestContext上下文 进行包装
 *
 * @author sobann
 */
public class GatewayContext implements Context<ServerWebExchange> {


    public static final String REQUEST_BODY_CACHE = "cachedRequestBodyObject";

    private ServerWebExchange exchange;

    public GatewayContext(ServerWebExchange exchange) {
        this.exchange = exchange;
    }


    @Override
    public String getServiceId() {
        Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
        return route.getId();
    }

    /**
     * 获取较为复杂，做缓存
     *
     * @return
     */
    @Override
    public String getRealIp() {
        Object attribute = getAttribute(REAL_IP);
        if (attribute != null) {
            return (String) attribute;
        }
        String realIp = getRealIp(exchange);
        if (realIp != null) {
            setAttribute(realIp, realIp);
        }
        return realIp;
    }

    private String getRealIp(ServerWebExchange exchange) {

        HttpHeaders headers = exchange.getRequest().getHeaders();
        String ipAddress = headers.getFirst("x-forwarded-for");

        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknow".equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }

        //对于通过多个代理的情况，第一个IP为客户端真实的IP地址，多个IP按照','分割
        if (null != ipAddress && ipAddress.length() > 15) {
            //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        if (StringUtils.isNotBlank(ipAddress)) {
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
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
        return exchange.getRequest().getPath().value();
    }

    @Override
    public String getBestUriPattern() {
        Object attribute = getAttribute(BEST_URI_PATTERN);
        return attribute == null ? null : (String) attribute;
    }

    @Override
    public void setBestUriPattern(String bestUriPattern) {
        if (bestUriPattern != null) {
            setAttribute(BEST_URI_PATTERN, bestUriPattern);
        }
    }

    @Override
    public String getRequestMethod() {
        return exchange.getRequest().getMethodValue();
    }

    /**
     * 需要前置预读过滤器
     *
     * @return
     */
    @Override
    public String getRequestBody() {
        return exchange.getAttribute(REQUEST_BODY_CACHE);
    }

    @Override
    public String getRequestContentType() {
        return exchange.getRequest().getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);
    }

    @Override
    public String getRequestParam(String name) {
        String param = null;
        //先从queryparam上获取
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        if (!CollectionUtils.isEmpty(queryParams) && queryParams.containsKey(name)) {
            param = queryParams.getFirst(name);
        }
        String key = "ZondyFormDatas";
        if (StringUtils.isBlank(param)) {
            String requestContentType = getRequestContentType();
            if (StringUtils.isNotBlank(requestContentType) && requestContentType.toLowerCase().contains("application/x-www-form-urlencoded")) {
                //所有表单提交参数进行缓存
                Object formDatas = getAttribute(key);
                if (formDatas == null) {
                    String requestBody = getRequestBody();
                    if (StringUtils.isNotBlank(requestBody)) {
                        String[] pairs = requestBody.split("&");
                        formDatas = new HashMap<String, String>();
                        for (String pair : pairs) {
                            String[] split = pair.split("=");
                            ((HashMap) formDatas).put(split[0], split[1]);
                        }
                        setAttribute(key, formDatas);
                    }
                }
                Map<String, String> map = (HashMap<String, String>) formDatas;
                param = map.get(name);
            }
        }
        return param;
    }

    @Override
    public String getRequestHeader(String name) {
        return exchange.getRequest().getHeaders().getFirst(name);
    }

    @Override
    public String getRequestCookie(String name) {
        HttpCookie first = exchange.getRequest().getCookies().getFirst(name);
        return first == null ? null : first.getValue();
    }

    @Override
    public String getErrorContent() {
        Object content = getAttribute(ERROR_CONTENT);
        return content == null ? null : (String) content;
    }

    @Override
    public void setErrorContent(String content) {
        setAttribute(ERROR_CONTENT, content);
    }

    @Override
    public String getErrorContentType() {
        Object type = getAttribute(ERROR_CONTENT_TYPE);
        return type == null ? null : (String) type;
    }

    @Override
    public void setErrorContentType(String type) {
        setAttribute(ERROR_CONTENT_TYPE, type);
    }

    @Override
    public Object getIdentityContext() {
        return exchange.getAttribute(IDENTITY_CONTEXT);
    }

    @Override
    public void setIdentityContext(Object context) {
        exchange.getAttributes().put(IDENTITY_CONTEXT, context);
    }

    @Override
    public void addRequestHeader(String name, String value) {
        ServerHttpRequest request = exchange.getRequest().mutate().header(name, value).build();
        ServerWebExchange build = exchange.mutate().request(request).build();
        this.exchange = build;
    }

    @Override
    public void addResponseHeader(String name, String value) {
        exchange.getResponse().getHeaders().add(name, value);
    }

    @Override
    public void addCookie(String name, String value, String domain, String path) {
        ResponseCookie cookie = ResponseCookie.from(name, value).domain(domain).path(path).build();
        exchange.getResponse().addCookie(cookie);
    }

    @Override
    public ServerWebExchange getOriginalContext() {
        return exchange;
    }

    @Override
    public Object getAttribute(String key) {
        return exchange.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        if (key != null && value != null) {
            exchange.getAttributes().put(key, value);
        }
    }
}
