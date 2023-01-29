package com.cheese.shiro.common.exception;

/**
 * 服务未注册至注册中心异常
 *
 * @author sobann
 */
public class ServiceNotRegisteredException extends RuntimeException {

    public ServiceNotRegisteredException() {
        super("current service instance is not registered to the registry");
    }

    public ServiceNotRegisteredException(String service) {
        super("current service instance" + service + " is not registered to the registry");
    }

}
