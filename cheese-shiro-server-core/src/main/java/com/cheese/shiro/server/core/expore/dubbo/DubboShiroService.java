package com.cheese.shiro.server.core.expore.dubbo;

import com.cheese.shiro.common.service.ShiroService;
import com.cheese.shiro.common.service.entity.InstancePerm;
import com.cheese.shiro.common.service.entity.QueryCondition;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;
import org.apache.dubbo.config.annotation.Service;

import java.util.List;

/**
 * dubbo权限资源服务提供
 * 此处使用装饰模式对实际权限资源进行封装，对外暴露dubbo接口
 *
 * @author sobann
 */
@Service(interfaceClass = ShiroService.class)
public class DubboShiroService implements ShiroService {

    private ShiroService shiroService;

    public void setShiroService(ShiroService shiroService) {
        this.shiroService = shiroService;
    }

    @Override
    public Boolean isPermitted(String identity, String identifier, String instanceId, String app) {
        return shiroService.isPermitted(identity, identifier, instanceId, app);
    }

    @Override
    public Boolean isAllPermitted(List<String> instaceIds, String identifier, String identity, String app) {
        return shiroService.isAllPermitted(instaceIds, identifier, identity, app);
    }

    @Override
    public Boolean isAllPermitted(List<InstancePerm> perms, String identity, String app) {
        return shiroService.isAllPermitted(perms, identity, app);
    }

    @Override
    public QueryRet getInstanceIdsWithAction(String entity, String action, String identity, String app) {
        return shiroService.getInstanceIdsWithAction(entity, action, identity, app);
    }

    @Override
    public QueryRet getScopeIdsWithAction(String entity, String action, String scope, String identity, boolean ignoreLevel, String app) {
        return shiroService.getScopeIdsWithAction(entity, action, scope, identity, ignoreLevel, app);
    }

    @Override
    public QueryCondition getConditions(String entity, String action, String identity, String app) {
        return shiroService.getConditions(entity, action, identity, app);
    }

    @Override
    public QueryMap getQueryMap(String entity, String action, String[] scopes, String identity, String app) {
        return shiroService.getQueryMap(entity, action, scopes, identity, app);
    }
}
