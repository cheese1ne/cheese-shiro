package com.cheese.shiro.server.gateway.assign;

import com.cheese.shiro.common.Context;
import com.cheese.shiro.common.manager.uri.UriManager;
import com.cheese.shiro.common.manager.uri.entity.AssignRouteMapping;
import com.cheese.shiro.server.gateway.host.CacheHostManager;
import com.cheese.shiro.server.gateway.host.Host;
import com.cheese.shiro.server.gateway.util.HashLoadBalanceUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

/**
 * 路由指派
 * @author sobann
 */
public class AssignRouter {

    private static final Logger logger = LoggerFactory.getLogger(AssignRouter.class);

    public static final String KEY ="AssignKey";

    private CacheHostManager cacheHostManager;

    private UriManager<AssignRouteMapping> uriManager;

    private DiscoveryClient discoveryClient;

    private volatile int virtualNodes = 5;

    public void setUriManager(UriManager<AssignRouteMapping> uriManager) {
        this.uriManager = uriManager;
    }

    public void setDiscoveryClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    public void setCacheHostManager(CacheHostManager cacheHostManager) {
        this.cacheHostManager = cacheHostManager;
    }

    public void setVirtualNodes(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    /**
     * 进行路由指派
     * @param context
     * @return
     */
    public Host assign(Context context){
        //查询@AssignRoute配置
        AssignRouteMapping assignRouteMapping = uriManager.getMatchUriMapping(context);
        if(assignRouteMapping==null){
            return null;
        }
        //获取AssignKey
        String assignKeyHeader = assignRouteMapping.getAssignKey();
        String assignKey = context.getRequestHeader(assignKeyHeader);
        if(StringUtils.isBlank(assignKey)){
            logger.warn("Can Not Find AssignKey With {} For {}",assignKeyHeader,context.getRequestUri());
            return null;
        }
        context.setAttribute(KEY,assignKey);
        //获取缓存host
        Host host = cacheHostManager.gainCache(assignKey);
        //没有缓存，选定一个服务实例
        if(host==null){
            String serviceId = context.getServiceId();
            final int num = virtualNodes;
            ServiceInstance serviceInstance = HashLoadBalanceUtil.getInstance(assignKey, discoveryClient.getInstances(serviceId),num);
            host = getCurrentHost(serviceInstance);
            cacheHostManager.createCache(assignKey,host);
        }
        return host;
    }

    /**
     * 解除路由指派
     * @param context
     */
    public void unAssign(Context context){
        Object key = context.getAttribute(AssignRouter.KEY);
        if(key!=null){
            cacheHostManager.clearCache((String) key);
        }
    }



    public Host getCurrentHost(ServiceInstance serviceInstance){
        if(serviceInstance==null){
            return null;
        }
        return new Host(serviceInstance.getHost(),serviceInstance.getPort());
    }
}
