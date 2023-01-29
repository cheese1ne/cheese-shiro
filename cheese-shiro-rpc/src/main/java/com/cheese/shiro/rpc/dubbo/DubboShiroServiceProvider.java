package com.cheese.shiro.rpc.dubbo;

import com.cheese.shiro.common.service.ShiroService;
import com.cheese.shiro.common.service.ShiroServiceProvider;
import org.apache.dubbo.config.annotation.Reference;

/**
 * 权限服务提供者默认Dubbo实现
 * @author sobann
 */
public class DubboShiroServiceProvider implements ShiroServiceProvider {
    @Reference(check = false)
    private ShiroService shiroService;
    @Override
    public ShiroService getShiroService() {
        return shiroService;
    }
}
