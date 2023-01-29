package com.cheese.shiro.server.core.expore.mvc;

import com.cheese.shiro.common.service.ShiroService;
import com.cheese.shiro.common.service.entity.InstancePerm;
import com.cheese.shiro.common.service.entity.QueryCondition;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * shiroService http服务(spring mvc) 暴露
 *
 * @author sobann
 */
@RestController
@RequestMapping("/shiro/server")
public class MvcResourceController {
    @Autowired
    private ShiroService shiroService;

    /**
     * 单条鉴权
     *
     * @param identity
     * @param identifier
     * @param instanceId
     * @param app
     * @return
     */
    @GetMapping(ShiroService.AUTH_URI)
    public Boolean isPermitted(@RequestParam String identity, @RequestParam String identifier, @RequestParam String instanceId, @RequestParam String app) {
        return shiroService.isPermitted(identity, identifier, instanceId, app);
    }

    /**
     * 批量鉴权
     *
     * @param instaceIds
     * @param identifier
     * @param identity
     * @param app
     * @return
     */
    @PostMapping(ShiroService.AUTH_BATCH_URI)
    public Boolean isAllPermitted(@RequestBody List<String> instaceIds, @RequestParam String identifier, @RequestParam String identity, @RequestParam String app) {
        return shiroService.isAllPermitted(instaceIds, identifier, identity, app);
    }

    /**
     * 批量鉴权
     *
     * @param perms
     * @param identity
     * @param app
     * @return
     */
    @PostMapping(ShiroService.AUTH_MULTI_URI)
    public Boolean isAllPermitted(@RequestBody List<InstancePerm> perms, @RequestParam String identity, @RequestParam String app) {
        return shiroService.isAllPermitted(perms, identity, app);
    }


    /**
     * 对象查询 entity:action
     *
     * @param entity
     * @param action
     * @param identity
     * @param app
     * @return
     */
    @GetMapping(ShiroService.QUERY_ENTITY_ACTION_URI)
    public QueryRet getInstanceWithAction(@RequestParam String entity, @RequestParam String action, @RequestParam String identity, @RequestParam String app) {
        return shiroService.getInstanceIdsWithAction(entity, action, identity, app);
    }


    /**
     * 对象范围查询 entity:action
     *
     * @param entity
     * @param action
     * @param scope
     * @param identity
     * @param ignoreLevel
     * @param app
     * @return
     */
    @GetMapping(ShiroService.QUERY_SCOPE_ACTION_URI)
    public QueryRet getScopeIdsWithAction(@RequestParam String entity, @RequestParam String action, @RequestParam String scope, @RequestParam String identity, @RequestParam boolean ignoreLevel, @RequestParam String app) {
        return shiroService.getScopeIdsWithAction(entity, action, scope, identity, ignoreLevel, app);
    }

    /**
     * 条件转换(列表类型)，用于es查询
     *
     * @param entity
     * @param action
     * @param identity
     * @param app
     * @return
     */
    @GetMapping(ShiroService.QUERY_CONDITION_URI)
    public QueryCondition getConditions(@RequestParam String entity, @RequestParam String action, @RequestParam String identity, @RequestParam String app) {
        return shiroService.getConditions(entity, action, identity, app);
    }

    /**
     * 条件转换(哈希类型)，用于es查询
     *
     * @param entity
     * @param action
     * @param identity
     * @param app
     * @return
     */
    @PostMapping(ShiroService.QUERY_MAP_URI)
    public QueryMap getQueryMap(@RequestParam String entity, @RequestParam String action, @RequestBody String[] scopes, @RequestParam String identity, @RequestParam String app) {
        return shiroService.getQueryMap(entity, action, scopes, identity, app);
    }


}
