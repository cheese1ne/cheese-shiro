package com.cheese.shiro.server.core.authentication;


import com.cheese.shiro.common.service.ShiroService;
import com.cheese.shiro.common.service.entity.*;
import com.cheese.shiro.server.core.authorization.Realm;
import com.cheese.shiro.server.core.manager.identifier.IdentifierManager;
import com.cheese.shiro.server.core.manager.instance.InstanceManager;
import com.cheese.shiro.server.core.manager.rule.RuleExplainer;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * 默认的权限调用实现
 * @author soban
 */
public class DefaultShiroService implements ShiroService {
	private Realm realm;
	private InstanceManager instanceManager;
	private IdentifierManager identifierManager;
	private int batchSize = 5;

	public DefaultShiroService(Realm realm, InstanceManager instanceManager, IdentifierManager identifierManager) {
		this.realm = realm;
		this.instanceManager = instanceManager;
		this.identifierManager = identifierManager;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	@Override
	public Boolean isPermitted(String identity, String identifier, String instanceId, String app) {
		Collection<? extends RulePerm> perms = realm.getPermByIdentity(identity, app);
		return isPermitted(identity,perms,identifier,instanceId);
	}

	public boolean isPermitted(String primary, Collection<? extends RulePerm> perms,String identifier, String instanceId){
		if(CollectionUtils.isEmpty(perms)){
			return false;
		}
		String instance = identifierManager.getOperateScope(identifier);
		for (RulePerm perm : perms) {
			if(perm.isValid() && identifierManager.implies(perm.getIdentifier(),identifier)){
				if(instanceManager.checkInstance(primary,perm.getRule(),instance,instanceId)){
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public Boolean isAllPermitted(List<String> instaceIds, String identifier, String identity, String app) {
		if(CollectionUtils.isEmpty(instaceIds)){
			return isPermitted(identity,identifier, RuleExplainer.Default,app);
		}
		if(instaceIds.size()==1){
			return isPermitted(identity,identifier,instaceIds.get(0),app);
		}
		if(instaceIds.size() < batchSize){
			List<InstancePerm> instancePerms = instaceIds.stream().map(instaceId -> new InstancePerm(identifier, instaceId)).collect(Collectors.toList());
			return isAllPermitted(instancePerms,identity,app);
		}
		Collection<? extends RulePerm> perms = realm.getPermByIdentity(identity, app);
		if(CollectionUtils.isEmpty(perms)){
			return false;
		}
		Set<String> rules = identifierManager.getImplyRules(perms, identifier);
		String instance = identifierManager.getOperateScope(identifier);
		return instanceManager.checkInstance(identity,rules,instance,instaceIds);
	}

	@Override
	public Boolean isAllPermitted(List<InstancePerm> instances, String identity, String app) {
		if(CollectionUtils.isEmpty(instances)){
			return true;
		}
		Collection<? extends RulePerm> perms = realm.getPermByIdentity(identity, app);
		for (InstancePerm instance : instances) {
			if(!isPermitted(identity,perms,instance.getIdentifier(),instance.getInstanceId())){
				return false;
			}
		}
		return true;
	}

	@Override
	public QueryRet getInstanceIdsWithAction(String entity, String action, String identity, String app) {
		//获取到用户符合实体操作条件的rule规则列表
		Collection<String> rules = getRulesWithEntityAndAction(identity, entity,action, app);
		//解析结果，若rule规则包含* 则all为true，str的bool值取自perm_entity中的key_type,empty与实例id列表负相关
		return instanceManager.getInstanceIds(identity,entity,rules);
	}

	@Override
	public QueryRet getScopeIdsWithAction(String entity, String action, String scope, String identity, boolean ignoreLevel, String app) {
		Collection<String> rules = getRulesWithEntityAndAction(identity, entity,action, app);
		return instanceManager.getScopeIds(identity,scope, rules,ignoreLevel);
	}

	@Override
	public QueryCondition getConditions(String entity, String action, String identity, String app){
		Collection<String> rules = getRulesWithEntityAndAction(identity, entity,action, app);
		return instanceManager.getConditions(identity,entity,rules);
	}

	@Override
	public QueryMap getQueryMap(String entity, String action, String[] scopes, String identity, String app) {
		Collection<String> rules = getRulesWithEntityAndAction(identity, entity,action, app);
		return instanceManager.getQueryMap(identity,entity,scopes,rules);
	}


	/**
	 * 从用户全部的权限中过滤出关于 entity:action的规则
	 * 用户的权限范围必须大于entity:action 才符合收集条件
	 * @param identity
	 * @param entity
	 * @param action
	 * @param app
	 * @return
	 */
	public Collection<String> getRulesWithEntityAndAction(String identity, String entity,String action, String app){
		//通过自定义Realm鉴权服务获取用户的全部权限信息perm_permission.identifier,perm_link_role_permission.rule
		Collection<? extends RulePerm> perms = realm.getPermByIdentity(identity, app);
		//拼接权限操作identifier即perm_permission中的identifier，此处为权限标识，权限规则细节在perm_link_role_permission.rule中完成定义
		String identifier = identifierManager.createIdentifier(entity, action);
		//通过identifier过滤出符合条件的rule规则列表
		return identifierManager.getImplyRules(perms,identifier);
	}

}
