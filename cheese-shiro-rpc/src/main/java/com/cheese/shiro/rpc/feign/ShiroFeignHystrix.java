package com.cheese.shiro.rpc.feign;

import com.cheese.shiro.common.service.entity.InstancePerm;
import com.cheese.shiro.common.service.entity.QueryCondition;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * feign调用权限服务默认熔断器
 * @author sobann
 */
public class ShiroFeignHystrix implements FallbackFactory<ShrioFeign> {
    private static final Logger logger = LoggerFactory.getLogger(ShiroFeignHystrix.class);
    private ShrioFeign hystrixShiroService;

    public ShiroFeignHystrix() {
        this.hystrixShiroService = new ShrioFeign() {
            @Override
            public Boolean isPermitted(String identity, String identifier, String instanceId, String app) {
                logger.error("ShrioFeign Is Unavailable Now, Return Default Fail Result");
                return false;
            }

            @Override
            public Boolean isAllPermitted(List<InstancePerm> perms, String identity, String app) {
                logger.error("ShrioFeign Is Unavailable Now, Return Default Fail Result");
                return false;
            }

            @Override
            public Boolean isAllPermitted(List<String> instaceIds, String identifier, String identity, String app) {
                logger.error("ShrioFeign Is Unavailable Now, Return Default Fail Result");
                return false;
            }

            @Override
            public QueryRet getInstanceIdsWithAction(String entity, String action, String identity, String app) {
                logger.error("ShrioFeign Is Unavailable Now, Return Default Empty Result");
                return new QueryRet();
            }

            @Override
            public QueryRet getScopeIdsWithAction(String entity, String action, String scope, String identity, boolean ignoreLevel, String app) {
                logger.error("ShrioFeign Is Unavailable Now, Return Default Empty Result");
                return new QueryRet();
            }

            @Override
            public QueryCondition getConditions(String entity, String action, String identity, String app) {
                logger.error("ShrioFeign Is Unavailable Now, Return Default Empty Result");
                return new QueryCondition();
            }

            @Override
            public QueryMap getQueryMap(String entity, String action, String[] scopes, String identity, String app) {
                logger.error("ShrioFeign Is Unavailable Now, Return Default Empty Result");
                return new QueryMap();
            }
        };
    }

    @Override
    public ShrioFeign create(Throwable throwable) {
        logger.error("ShiroFeign Is Error !!! Cause Is {}",throwable.getMessage());
        return this.hystrixShiroService;
    }
}
