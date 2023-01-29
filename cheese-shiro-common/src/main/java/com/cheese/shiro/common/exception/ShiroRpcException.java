package com.cheese.shiro.common.exception;

/**
 * Rpc调用异常
 *
 * @author sobann
 */
public class ShiroRpcException extends RuntimeException {

    public ShiroRpcException() {
        super("rpc service invocation failed");
    }

    public ShiroRpcException(String service) {
        super("The Rpc service {" + service + "} invocation failed");
    }

}
