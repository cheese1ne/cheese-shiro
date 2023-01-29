package com.cheese.shiro.common.service;



import com.cheese.shiro.common.service.entity.InstancePerm;
import com.cheese.shiro.common.service.entity.QueryCondition;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;

import java.util.List;

/**
 * 权限调用接口
 * @author sobann
 */
public interface ShiroService {

	/**
	 *	单条鉴权：/auth
	 * @param identity 用户凭证（id）
	 * @param identifier
	 * @param instanceId
	 * @param app 所在服务名称
	 * @return
	 */
	Boolean isPermitted(String identity, String identifier, String instanceId, String app);
	String AUTH_URI="/auth";

	/**
	 * 批量鉴权：/auth/batch
	 * @param instaceIds
	 * @param identifier
	 * @param identity
	 * @param app
	 * @return
	 */
	Boolean isAllPermitted(List<String> instaceIds, String identifier, String identity, String app);
	String AUTH_BATCH_URI ="/auth/batch";

	/**
	 * 批量鉴权：/auth/multi
	 * @param perms
	 * @param identity
	 * @param app
	 * @return
	 */
	Boolean isAllPermitted(List<InstancePerm> perms, String identity, String app);
	String AUTH_MULTI_URI ="/auth/multi";

	/**
	 * entity:action 数据/操作权限：/entity/action
	 * @param entity 范围对象
	 * @param action 动作
	 * @param identity
	 * @param app
	 * @return
	 */
	QueryRet getInstanceIdsWithAction(String entity, String action, String identity, String app);
	String QUERY_ENTITY_ACTION_URI ="/entity/action";

	/**
	 * entity:action 数据权限 向 scope转换：/scope/action
	 * @param entity
	 * @param scope
	 * @param action
	 * @param identity
	 * @param ignoreLevel
	 * @param app
	 * @return
	 */
	QueryRet getScopeIdsWithAction(String entity, String action, String scope, String identity, boolean ignoreLevel, String app);
	String QUERY_SCOPE_ACTION_URI ="/scope/action";

	/**
	 * 将对应权限数据转换为map条件，配合es插件，转换为filter：/condition
	 * @param entity
	 * @param action
	 * @param identity
	 * @param app
	 * @return
	 */
	QueryCondition getConditions(String entity, String action, String identity, String app);
	String QUERY_CONDITION_URI ="/condition";

	/**
	 * 将权限数据，转换至指定几个scope中：/map
	 * @param entity
	 * @param action
	 * @param scopes
	 * @param identity
	 * @param app
	 * @return
	 */
	QueryMap getQueryMap(String entity, String action, String[] scopes, String identity, String app);
	String QUERY_MAP_URI ="/map";
}
