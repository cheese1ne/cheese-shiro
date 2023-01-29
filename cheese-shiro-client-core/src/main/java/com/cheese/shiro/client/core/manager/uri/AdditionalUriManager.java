package com.cheese.shiro.client.core.manager.uri;


import com.cheese.shiro.common.manager.uri.entity.AssignRouteMapping;
import com.cheese.shiro.common.manager.uri.entity.AuthUriMapping;
import com.cheese.shiro.common.manager.uri.entity.GatewayLogUriMapping;
import com.cheese.shiro.common.manager.uri.entity.UriMapping;

import java.util.List;

/**
 * 向网关中心额外的添加@Auth,@Login,@ServerUri信息
 * 实现接口，将会将相关信息传递至网关中心
 * 例如未接入的java服务
 *
 * @author sobann
 */
public interface AdditionalUriManager {
    /**
     * 获取其他服务名称
     *
     * @return
     */
    List<String> getAppNames();

    /**
     * 获取对应服务@login
     *
     * @param appName
     * @return
     */
    List<UriMapping> getLoginUri(String appName);

    /**
     * 获取对应服务@ServerUri
     *
     * @param appName
     * @return
     */
    List<UriMapping> getServerUri(String appName);

    /**
     * 获取对应服务@Auth
     *
     * @param appName
     * @return
     */
    List<AuthUriMapping> getAuthUri(String appName);

    /**
     * 获取对应服务@GatewayLog
     *
     * @param appName
     * @return
     */
    List<GatewayLogUriMapping> getGatewayLogUri(String appName);

    /**
     * 获取对应服务@AssignRoute
     *
     * @param appName
     * @return
     */
    List<AssignRouteMapping> getAssignRoutUri(String appName);

    /**
     * 获取对应uriPattern
     *
     * @param appName
     * @return
     */
    List<UriMapping> getUriPattern(String appName);


}
