package com.cheese.shiro.common.domain;


import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.manager.uri.entity.GatewayLogInfo;
import com.cheese.shiro.common.util.ParamUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 网关日志内容
 *
 * @author sobann
 */
public class GatewayLogContent implements Serializable {
    private static final long serialVersionUID = -7472266615170610142L;
    private String id;
    /**
     * 网关节点
     */
    private String gatewayNode;
    /**
     * uri格式
     */
    private String uriPattern;
    /**
     * 请求方式
     */
    private String httpMethod;
    /**
     * 请求服务
     */

    private String service;
    /**
     * 对应类
     */
    private String clazz;
    /**
     * 对应方法
     */
    private String method;
    /**
     * 数据实体
     */
    private String entity;
    /**
     * 操作
     */
    private String action;
    /**
     * 权限校验信息
     */
    private String auth;
    /**
     * 实例id
     */
    private List<String> instanceIds;
    /**
     * 用户
     */
    private String user;
    /**
     * 请求ip
     */
    private String ip;
    /**
     * 请求uri
     */
    private String uri;
    /**
     * 请求格式
     */
    private String contentType;
    /**
     * 请求体
     */
    private String requestBody;
    /**
     * 响应码
     */
    private int responseStatus;
    /**
     * 响应节点地址
     */
    private String routeNode;
    /**
     * 响应体
     */
    private String responseBody;
    /**
     * 异常
     */
    private String exception;
    /**
     * 开始时间
     */
    private String startTime;
    /**
     * 结束时间
     */
    private String endTime;
    /**
     * 属性
     */
    private Map<String, Object> props;

    public GatewayLogContent() {
    }

    public GatewayLogContent(Context context, GatewayLogInfo logInfo) {
        this.uriPattern = context.getBestUriPattern();
        this.httpMethod = context.getRequestMethod();
        this.clazz = logInfo.getClazz();
        this.method = logInfo.getMethod();
        this.service = context.getServiceId();
        this.ip = context.getRealIp();
        this.uri = context.getRequestUri();
        this.contentType = context.getRequestContentType();
        this.entity = ParamUtils.getValueWithExpress(logInfo.getEntity(), context);
        this.action = ParamUtils.getValueWithExpress(logInfo.getAction(), context);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUriPattern() {
        return uriPattern;
    }

    public void setUriPattern(String uriPattern) {
        this.uriPattern = uriPattern;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public List<String> getInstanceIds() {
        return instanceIds;
    }

    public void setInstanceIds(List<String> instanceIds) {
        this.instanceIds = instanceIds;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getRouteNode() {
        return routeNode;
    }

    public void setRouteNode(String routeNode) {
        this.routeNode = routeNode;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getGatewayNode() {
        return gatewayNode;
    }

    public void setGatewayNode(String gatewayNode) {
        this.gatewayNode = gatewayNode;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public Map<String, Object> getProps() {
        return props;
    }

    public void setProps(Map<String, Object> props) {
        this.props = props;
    }
}
