package com.cheese.shiro.client.core.checker.register;

import com.cheese.shiro.client.core.props.RegisterProp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

/**
 * http注册状态检查器，用于解决权限客户端权限信息注册时
 * 由于尚未注册到注册中心中，导致负载均衡restTemplate请求报错的问题
 * 思路：在注册信息之前，对注册状态进行检查
 * 目前考虑通过访问注册中心的endPoint端口来判断当前服务时候注册至注册中心
 * 通过返回结果抛出响应的错误，让注册线程等待执行
 *
 * @author sobann
 */
public class HttpServiceRegisterStatusChecker implements ServiceRegisterStatusChecker {

    private static Logger log = LoggerFactory.getLogger(HttpServiceRegisterStatusChecker.class);
    private DiscoveryClient discoveryClient;
    private RegisterProp registerProp;
    private String host;

    public HttpServiceRegisterStatusChecker(DiscoveryClient discoveryClient, RegisterProp registerProp, String host) {
        this.discoveryClient = discoveryClient;
        this.registerProp = registerProp;
        this.host = host;
    }

    public void setRegisterProp(RegisterProp registerProp) {
        this.registerProp = registerProp;
    }

    @Override
    public Boolean check() {
        if (!registerProp.getDoChecker()) {
            return Boolean.TRUE;
        }
        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            if (service.equals(registerProp.getService())) {
                //已有serviceId服务，但需要进一步验证是否是当前注册服务，通过ip+port进行验证
                List<ServiceInstance> instances = discoveryClient.getInstances(service);
                for (ServiceInstance instance : instances) {
                    //验证ip和端口
                    String ipPort = instance.getHost() + ":" + instance.getPort();
                    if (ipPort.equals(host)) {
                        log.info("current service instance is already registered to the registry, now prepare to register auth info to :{}", registerProp.getServer());
                        return Boolean.TRUE;
                    }
                }
            }
        }
        //循环完毕未找到注册实例，返回false
        log.warn("current service instance :{} is not registered to the registry, the host is {}", registerProp.getService(), host);
        return Boolean.FALSE;
    }

}
