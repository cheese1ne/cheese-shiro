package com.cheese.shiro.rpc.mock;


import com.cheese.shiro.common.service.ShiroService;
import com.cheese.shiro.common.service.ShiroServiceProvider;
import com.cheese.shiro.common.service.entity.InstancePerm;
import com.cheese.shiro.common.service.entity.QueryCondition;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;

import java.util.List;

/**
 * 权限服务提供者默认Mock实现
 * @author sobann
 */
public class MockShiroServiceProvider implements ShiroServiceProvider {

    private ShiroService shiroService;

    public MockShiroServiceProvider() {
        shiroService = new ShiroService() {
            @Override
            public Boolean isPermitted(String identity, String identifier, String instanceId, String app) {
                return true;
            }

            @Override
            public Boolean isAllPermitted(List<String> instaceIds, String identifier, String identity, String app) {
                return true;
            }

            @Override
            public Boolean isAllPermitted(List<InstancePerm> perms, String identity, String app) {
                return true;
            }

            @Override
            public QueryRet getInstanceIdsWithAction(String entity, String action, String identity, String app) {
                return new QueryRet(true,true);
            }

            @Override
            public QueryRet getScopeIdsWithAction(String entity, String action, String scope, String identity, boolean ignoreLevel, String app) {
                return new QueryRet(true,true);
            }

            @Override
            public QueryCondition getConditions(String entity, String action, String identity, String app) {
                return new QueryCondition(true);
            }

            @Override
            public QueryMap getQueryMap(String entity, String action, String[] scopes, String identity, String app) {
                return new QueryMap(true);
            }
        };
    }

    @Override
    public ShiroService getShiroService() {
        return shiroService;
    }
}
