package com.cheese.shiro.server.core.expore.jersey;

import com.cheese.shiro.common.service.ShiroService;
import com.cheese.shiro.common.service.entity.InstancePerm;
import com.cheese.shiro.common.service.entity.QueryCondition;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;
import org.springframework.beans.factory.annotation.Autowired;

import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * shiroService http服务(jersey) 暴露
 *
 * @author sobann
 */
@Singleton
@Path("/server")
public class JerseyServerResource {

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
    @GET
    @Path(ShiroService.AUTH_URI)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean isPermitted(@QueryParam("identity") String identity, @QueryParam("identifier") String identifier, @QueryParam("instanceId") String instanceId, @QueryParam("app") String app) {
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
    @POST
    @Path(ShiroService.AUTH_BATCH_URI)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean isAllPermitted(List<String> instaceIds, @QueryParam("identifier") String identifier, @QueryParam("identity") String identity, @QueryParam("app") String app) {
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
    @POST
    @Path(ShiroService.AUTH_MULTI_URI)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Boolean isAllPermitted(List<InstancePerm> perms, @QueryParam("identity") String identity, @QueryParam("app") String app) {
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
    @GET
    @Path(ShiroService.QUERY_ENTITY_ACTION_URI)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryRet getInstanceWithAction(@QueryParam("entity") String entity, @QueryParam("action") String action, @QueryParam("identity") String identity, @QueryParam("app") String app) {
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
    @GET
    @Path(ShiroService.QUERY_SCOPE_ACTION_URI)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryRet getScopeIdsWithAction(@QueryParam("entity") String entity, @QueryParam("action") String action, @QueryParam("scope") String scope, @QueryParam("identity") String identity, @QueryParam("ignoreLevel") boolean ignoreLevel, @QueryParam("app") String app) {
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
    @GET
    @Path(ShiroService.QUERY_CONDITION_URI)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryCondition getConditions(@QueryParam("entity") String entity, @QueryParam("action") String action, @QueryParam("identity") String identity, @QueryParam("app") String app) {
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
    @POST
    @Path(ShiroService.QUERY_MAP_URI)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public QueryMap getQueryMap(@QueryParam("entity") String entity, @QueryParam("action") String action, String[] scopes, @QueryParam("identity") String identity, @QueryParam("app") String app) {
        return shiroService.getQueryMap(entity, action, scopes, identity, app);
    }
}
