package com.cheese.shiro.rpc.feign;


import com.cheese.shiro.common.service.ShiroService;
import com.cheese.shiro.common.service.ShiroServiceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * 权限服务提供者默认feign实现
 * 解决懒加载问题
 * @author sobann
 */
public class FeignShiroServiceProvider implements ShiroServiceProvider {
    @Lazy
    @Autowired
    private ShiroService shiroService;

    @Override
    public ShiroService getShiroService() {
        return shiroService;
    }
}
