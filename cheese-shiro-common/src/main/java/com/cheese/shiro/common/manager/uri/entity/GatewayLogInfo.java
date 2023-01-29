package com.cheese.shiro.common.manager.uri.entity;


import com.cheese.shiro.common.anno.GatewayLog;

import java.io.Serializable;

/**
 * 网关统一日志记录GatewayLog信息抽取
 *
 * @author sobann
 */
public class GatewayLogInfo implements Serializable {
    private static final long serialVersionUID = 1826320347386582867L;
    private String entity;
    private String action;
    private String instanceId;
    private String clazz;
    private String method;
    private boolean requestBody;
    private boolean responseBody;

    public GatewayLogInfo() {
    }

    public GatewayLogInfo(GatewayLog gatewayLog) {
        this.entity = gatewayLog.entity();
        this.action = gatewayLog.action();
        this.instanceId = gatewayLog.instanceId();
        this.requestBody = gatewayLog.requestBody();
        this.responseBody = gatewayLog.responseBody();
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

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public boolean isRequestBody() {
        return requestBody;
    }

    public void setRequestBody(boolean requestBody) {
        this.requestBody = requestBody;
    }

    public boolean isResponseBody() {
        return responseBody;
    }

    public void setResponseBody(boolean responseBody) {
        this.responseBody = responseBody;
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

    /**
     * 重置属性
     * @param gatewayLog
     */
    public void reset(GatewayLog gatewayLog){
        if(reset(gatewayLog.entity())){
            this.entity = gatewayLog.entity();
        }
        if(reset(gatewayLog.action())){
            this.action = gatewayLog.action();
        }
        if(reset(gatewayLog.instanceId())){
            this.instanceId = gatewayLog.instanceId();
        }
        this.requestBody = gatewayLog.requestBody();
        this.responseBody = gatewayLog.responseBody();
    }

    private boolean reset(String value){
        if(value==null || "".equals(value.trim()) ){
            return false;
        }
        return true;
    }
}
