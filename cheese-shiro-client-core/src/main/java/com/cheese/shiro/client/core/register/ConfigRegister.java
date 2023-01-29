package com.cheese.shiro.client.core.register;

import java.util.Map;

/**
 * 实现此接口，返回需要注册的信息
 *
 * @author sobann
 */
public interface ConfigRegister {
    /**
     * 实现此接口，返回需要注册的信息
     *
     * @return
     */
    Map<String, Object> getRegisterConfig();
}
