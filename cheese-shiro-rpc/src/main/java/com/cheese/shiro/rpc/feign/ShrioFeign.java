package com.cheese.shiro.rpc.feign;


import com.cheese.shiro.common.service.ShiroService;
import com.cheese.shiro.common.service.entity.InstancePerm;
import com.cheese.shiro.common.service.entity.QueryCondition;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * feign 调用服务
 * Created by ll on 2018/5/24.
 */
@FeignClient(value = "${cheese.shiro.rpc.server}" ,contextId = "shiroServerFeign",path = "shiro/server",fallbackFactory = ShiroFeignHystrix.class)
public interface ShrioFeign extends ShiroService {

    /**
     *	单条鉴权
     * @param identity 用户凭证（id）
     * @param identifier
     * @param instanceId
     * @param app 所在服务名称
     * @return
     */
    @Override
    @RequestMapping(value = ShiroService.AUTH_URI,method = RequestMethod.GET)
    public Boolean isPermitted(@RequestParam("identity") String identity, @RequestParam("identifier") String identifier, @RequestParam("instanceId") String instanceId, @RequestParam("app") String app);

    /**
     * 批量鉴权
     * @param perms
     * @param identity
     * @param app
     * @return
     */
    @Override
    @RequestMapping(value =ShiroService.AUTH_MULTI_URI,method = RequestMethod.POST)
    public Boolean isAllPermitted(@RequestBody List<InstancePerm> perms, @RequestParam("identity") String identity, @RequestParam("app") String app);

    /**
     * 批量鉴权
     * @param instaceIds
     * @param identifier
     * @param identity
     * @param app
     * @return
     */
    @Override
    @RequestMapping(value =ShiroService.AUTH_BATCH_URI,method = RequestMethod.POST)
    public Boolean isAllPermitted(@RequestBody List<String> instaceIds, @RequestParam("identifier") String identifier, @RequestParam("identity") String identity, @RequestParam("app") String app);

    /**
     * entity:action 数据/操作权限
     * @param entity 范围对象
     * @param action 动作
     * @param identity
     * @param app
     * @return
     */
    @Override
    @RequestMapping(value =ShiroService.QUERY_ENTITY_ACTION_URI,method = RequestMethod.GET)
    public QueryRet getInstanceIdsWithAction(@RequestParam("entity") String entity, @RequestParam("action") String action, @RequestParam("identity") String identity, @RequestParam("app") String app);

    /**
     * entity:action 数据权限 向 scope转换
     * @param entity
     * @param scope
     * @param action
     * @param identity
     * @param ignoreLevel
     * @param app
     * @return
     */
    @Override
    @RequestMapping(value =ShiroService.QUERY_SCOPE_ACTION_URI,method = RequestMethod.GET)
    public QueryRet getScopeIdsWithAction(@RequestParam("entity") String entity, @RequestParam("action") String action, @RequestParam("scope") String scope, @RequestParam("identity") String identity, @RequestParam("ignoreLevel") boolean ignoreLevel, @RequestParam("app") String app);

    /**
     * 将对应权限数据转换为map条件，配合es插件，转换为filter
     * @param entity
     * @param action
     * @param identity
     * @param app
     * @return
     */
    @Override
    @RequestMapping(value = QUERY_CONDITION_URI,method = RequestMethod.GET)
    public QueryCondition getConditions(@RequestParam("entity") String entity, @RequestParam("action") String action, @RequestParam("identity") String identity, @RequestParam("app") String app);

    /**
     * 将权限数据，转换至指定几个scope中
     * @param entity
     * @param action
     * @param scopes
     * @param identity
     * @param app
     * @return
     */
    @Override
    @RequestMapping(value = QUERY_MAP_URI,method = RequestMethod.POST)
    public QueryMap getQueryMap(@RequestParam("entity") String entity, @RequestParam("action") String action, @RequestBody String[] scopes, @RequestParam("identity") String identity, @RequestParam("app") String app);

}
