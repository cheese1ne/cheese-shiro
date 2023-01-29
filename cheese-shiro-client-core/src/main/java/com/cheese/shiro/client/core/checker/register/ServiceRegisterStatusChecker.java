package com.cheese.shiro.client.core.checker.register;

/**
 * 服务注册状态检查器顶层接口，目前默认只有http注册的实现
 *
 * @author sobann
 */
public interface ServiceRegisterStatusChecker {
    /**
     * 进行注册状态检查
     *
     * @return
     */
    Boolean check();

}
