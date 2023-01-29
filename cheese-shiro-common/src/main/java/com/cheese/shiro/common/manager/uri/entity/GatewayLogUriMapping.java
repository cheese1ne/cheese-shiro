package com.cheese.shiro.common.manager.uri.entity;

import java.util.Set;

/**
 * 网关日志信息URI资源，由接入的服务向中心传递
 *
 * @author sobann
 */
public class GatewayLogUriMapping extends UriMapping {
    private static final long serialVersionUID = 1L;
    private GatewayLogInfo gatewayLogInfo;


    public GatewayLogUriMapping() {
    }

    public GatewayLogUriMapping(GatewayLogInfo gatewayLogInfo) {
        this.gatewayLogInfo = gatewayLogInfo;
    }

    public GatewayLogUriMapping(Set<String> patterns, Set<String> requestMethods, GatewayLogInfo gatewayLogInfo) {
        super(patterns, requestMethods);
        this.gatewayLogInfo = gatewayLogInfo;
    }

    public GatewayLogInfo getGatewayLogInfo() {
        return gatewayLogInfo;
    }

    public void setGatewayLogInfo(GatewayLogInfo gatewayLogInfo) {
        this.gatewayLogInfo = gatewayLogInfo;
    }

    public void addUriMapping(UriMapping uriMapping){
        super.setPatterns(uriMapping.getPatterns());
        super.setRequestMethods(uriMapping.getRequestMethods());
    }
}
