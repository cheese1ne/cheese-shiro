package com.cheese.shiro.common.manager.uri.entity;


import com.cheese.shiro.common.anno.AssignRoute;

/**
 * 网关路由指派信息提取
 * @author sobann
 */
public class AssignRouteMapping extends UriMapping {
    private static final long serialVersionUID = 600235661271490775L;
    private String assignKey;

    public String getAssignKey() {
        return assignKey;
    }

    public void setAssignKey(String assignKey) {
        this.assignKey = assignKey;
    }

    public AssignRouteMapping() {
    }

    public AssignRouteMapping(UriMapping uriMapping, AssignRoute assignRoute){
        setPatterns(uriMapping.getPatterns());
        setRequestMethods(uriMapping.getRequestMethods());
        this.assignKey = assignRoute.assignKey();
    }
}
