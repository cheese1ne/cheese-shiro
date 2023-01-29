package com.cheese.shiro.server.core.manager.instance;


import com.cheese.shiro.common.domain.SPage;
import com.cheese.shiro.common.service.entity.FieldValue;
import com.cheese.shiro.common.service.entity.QueryCondition;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;
import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.table.EntityInfo;
import com.cheese.shiro.common.table.TableConverter;
import com.cheese.shiro.server.core.manager.rule.RuleExplainer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 检查实例操作是否合法
 * 获取权限内可查看的实例id集合
 * @author ll
 * 如果所检查的对象没有数据库映射(虚拟实体时)
 * 实例检查统一返回false,或者空 queryRet
 *
 */

public class DefaultInstanceManager implements InstanceManager {
	private static final Logger logger = LoggerFactory.getLogger(DefaultInstanceManager.class);
	private TableConverter tableConverter;
	private RuleExplainer ruleExplainer;

	public DefaultInstanceManager(TableConverter tableConverter, RuleExplainer ruleExplainer) {
		this.tableConverter = tableConverter;
		this.ruleExplainer = ruleExplainer;
	}

	private String defaultAction = "get";

	public String getDefaultAction() {
		return defaultAction;
	}

	public void setDefaultAction(String defaultAction) {
		this.defaultAction = defaultAction;
	}

	@Override
	public boolean checkInstance(String identity, String ruleExpr, String instance, String instanceId) {
		try {
			if(StringUtils.isBlank(ruleExpr)){
				return false;
			}
			// * 代表任意数据单位  对任意实例具有权限，直接返回true
			if(RuleExplainer.All.equals(ruleExpr)){
				return true;
			}
			//* 代表检查所有数据权限，如果has.rule != *,则直接失败
			if(RuleExplainer.All.equals(instanceId)){
				return false;
			}

			//_ 代表 无法对该操作实例做限制, 拥有该方法权限即可任意添加
			if(RuleExplainer.Default.equals(instanceId)){
				return true;
			}
			//虚拟实体或者错误配置，返回false
			if(!instanceConfigCheck(instance)){
				return getBooleanWithBadConfig();
			}
			//可能存在多条件  predefine:space:self & propertity:file:type='zip'
			String[] rules = ruleExpr.split(RuleExplainer.Addition);
			for (String rule : rules) {
				String stmt = ruleExplainer.buildSqlForCheckInstance(identity, instance, rule);
				if (stmt == null) {
					return false;
				}
				SqlTranslate sqlTranslate = ruleExplainer.getSqlTranslateByScopeAndRule(instance, rule);
				boolean resultContainsInstace = sqlTranslate.isResultContainsInstace(stmt, instanceId);
				if (!resultContainsInstace) {
					return false;
				}
			}
			return true;
		} catch (Exception e) {
			logger.error("Check Instane Error",e);
			return false;
		}
	}

	@Override
	public boolean checkInstance(String identity, Set<String> rules, String instance, Collection<String> instanceIds) {
		try {
			//获取所有集合
			QueryRet scopeIds = getScopeIds(identity,instance, rules,false);
			if(scopeIds.isAll()){
                return true;
            }
			if(scopeIds.isEmpty()){
                return false;
            }
			List<String> ables = scopeIds.getEntity();
			//快速失败
			if(instanceIds.size() > ables.size()){
                return false;
            }
			//有一个不在集合内，直接返回
			for (String id : instanceIds) {
                if(!ables.contains(id)){
                    return false;
                }
            }
			return true;
		} catch (Exception e) {
			logger.error("Check Instane Error",e);
			return false;
		}
	}


	@Override
	public QueryRet getInstanceIds(String identity, String entity, Collection<String> rules) {
		return getScopeIds(identity,entity,rules,false);
	}

	@Override
	public QueryRet getScopeIds(String identity, String scope, Collection<String> rules, boolean ignoreLevel){
		//虚拟实体或者错误配置，返回空集合
		if( !instanceConfigCheck(scope)){
			return getQueryWithBadConfig();
		}
		//如果有一条权限已经包含所有数据范围，即对结果集不做任何限制
		Boolean pkIsStr = tableConverter.idTypeIsStr(scope);
		if(rules.contains(RuleExplainer.All)){
			//返回全部数据范围，在权限客户端不对原查询sql做任何处理
			return new QueryRet(true,pkIsStr);
		}

		List<String> checkedRules = new ArrayList<>();
		//总结果集
		Set<String> total = new HashSet<>();
		//外部循环别名rule，大循环遍历用户所属的全部权限 perm_permission 的identifier
		rule:for (String ruleStr : rules) {
			try {
				//数据范围非法或已处理，直接跳过该权限
				if(StringUtils.isBlank(ruleStr) || checkedRules.contains(ruleStr)){
					continue ;
				}
				String[] ruleExprs = ruleStr.split(RuleExplainer.Addition);
				//出否出现情况包含所有范围
				Boolean containAll = false;
				//单条权限结果集
				Set<String> ids = new HashSet<>();
				for (int i = 0; i < ruleExprs.length; i++) {
					String rule = ruleExprs[i];
					//根据规则构建出sql，关键表 perm_entity,perm_link
					String stmt = ruleExplainer.buildSqlForQueryScope(identity,scope,rule,ignoreLevel);
					//有一条结果集解析错误，直接下一个权限
					if(StringUtils.isBlank(stmt)){
						continue rule;
					}
					//出现特殊条件不能与范围进行匹配，扩展为全部范围
					if(RuleExplainer.Addition.equals(stmt)){
						//滤过该条件，依靠其他条件进行判断
						//没有关系的范围之间需要转化，暂时不进行，防止数据越权 2019.03.27
						//containAll = true;
						continue;
					}
					//单条规则结果集
					Set<String> result =null;
					try {
						//获取数据转换器，根据perm_link_role_permission中的rule中的实体获取数据转换器
						SqlTranslate sqlTranslate = ruleExplainer.getSqlTranslateByScopeAndRule(scope, rule);
						//获取sql执行结果集
						result=	sqlTranslate.querySql(stmt);
					} catch (Exception e) {
						logger.error("Excute Sql Error",e);
						continue rule;
					}
					if(CollectionUtils.isEmpty(result)){
						//多规则同时限制时，有一条规则不满足,结果为0,则该权限下无结果集
						continue rule;
					}
					if(ids.size()==0){
						//不取交集
						ids = result;
						continue;
					}
					//第二条规则的结果集与第一条规则的结果集取交集
					ids = getIntersection(ids, result);
				}
				//每条权限的结果集放入总结果去重，全范围忽略
				if(!CollectionUtils.isEmpty(ids)){
					total.addAll(ids);
				}else if(containAll){
					//如果存在单一的 包含全范围的权限
					return new QueryRet(true,pkIsStr);
				}
				checkedRules.add(ruleStr);
			} catch (Exception e) {
				logger.error("Get Scope Ids Error",e);
			}
		}
		return new QueryRet(new ArrayList<>(total),pkIsStr);
	}


	//取交集数据
	private Set<String>  getIntersection(Set<String> ids, Set<String> result) {
		return ids.stream().filter(result::contains).collect(Collectors.toSet());
	}



	@Override
	public List<EntityInfo> getEntityInfo(String target, QueryRet queryRet, String keyword) {
		if(queryRet.isEmpty()){
			return new ArrayList<>();
		}
		SqlTranslate sqlTranslate = ruleExplainer.getSqlTranslate(target);
		return sqlTranslate.queryEntityInfo(target, queryRet.getEntity(),queryRet.isAll(),keyword);
	}


	@Override
	public SPage<EntityInfo> getPageOfEntityInfo(String target, QueryRet queryRet, String keyword, int page, int size) {
		if(queryRet.isEmpty()){
			return new SPage<>(page,size,0,new ArrayList<>());
		}
		SqlTranslate sqlTranslate = ruleExplainer.getSqlTranslate(target);
		return sqlTranslate.queryPageOfEntityInfo(target,queryRet.getEntity(),queryRet.isAll(),keyword,page,size);
	}

	@Override
	public Map<String, String> getTreeIdsOfEntity(String entity) {
		SqlTranslate sqlTranslate = ruleExplainer.getSqlTranslate(entity);
		return sqlTranslate.queryTreeIdsMap(entity);
	}

	@Override
	public QueryRet getScopeIdsOnlyForRule(String identity, String rule) {
		if(RuleExplainer.All.equals(rule)){
			return new QueryRet(true,false);
		}
		String scope = rule.split(RuleExplainer.Separator)[1];
		if(!instanceConfigCheck(scope)){
			return getQueryWithBadConfig();
		}
		Boolean isStr = tableConverter.idTypeIsStr(scope);
		String sql = ruleExplainer.buildSqlForRule(identity, rule);
		SqlTranslate sqlTranslate = ruleExplainer.getSqlTranslate(scope);
		Set<String> result = sqlTranslate.querySql(sql);
		return new QueryRet(new ArrayList<>(result),isStr);
	}


	@Override
	public QueryCondition getConditions(String identity, String entity, Collection<String> rules){
		if(!instanceConfigCheck(entity) || CollectionUtils.isEmpty(rules)){
			return new QueryCondition();
		}
		if(rules.contains(RuleExplainer.All)){
			return new QueryCondition(true);
		}
		QueryCondition queryCondition = new QueryCondition();
		List<String> checkedRule = new ArrayList<>();
		rule:for (String ruleStr : rules) {
			//数据范围非法或已处理，直接跳过该权限
			if(StringUtils.isBlank(ruleStr) || checkedRule.contains(ruleStr)){
				continue;
			}
			List<FieldValue> fieldValues = new ArrayList<>();
			String[] ruleExprs = ruleStr.split(RuleExplainer.Addition);
			for (String rule : ruleExprs) {
				FieldValue fieldValue =null;
				try {
					fieldValue = ruleExplainer.buildConditions(entity, identity, rule);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//解析出现错误，整条权限跳过
				if(fieldValue==null){
					continue rule;
				}
				fieldValues.add(fieldValue);
			}
			queryCondition.addCondition(fieldValues);
			checkedRule.add(ruleStr);
		}
		return queryCondition;
	}


	@Override
	public QueryMap getQueryMap(String identity, String entity, String[] scopes, Collection<String> rules) {
		if(!instanceConfigCheck(entity) || CollectionUtils.isEmpty(rules)){
			return new QueryMap();
		}
		if(rules.contains(RuleExplainer.All)){
			return new QueryMap(true);
		}
		QueryMap queryMap = new QueryMap();
		//范围列表
		List<String> realScopes = new ArrayList<>();
		//属性列表
		List<String> props = new ArrayList<>();
		for (String scope : scopes) {
			if(scope.startsWith("@")){
				props.add(scope.substring(1));
			}else {
				realScopes.add(scope);
				Boolean isStr = tableConverter.idTypeIsStr(scope);
				queryMap.addIsStr(scope,isStr);
			}
		}
		List<String> checkedRule = new ArrayList<>();
		rule:for (String ruleStr : rules) {
			//数据范围非法或已处理，直接跳过该权限
			if(StringUtils.isBlank(ruleStr) || checkedRule.contains(ruleStr)){
				continue;
			}
			if(ruleStr.contains(RuleExplainer.Addition)){
				//TODO
			}else {
				ruleExplainer.buildQueryMap(entity,identity,ruleStr,realScopes,props,queryMap);
			}
			checkedRule.add(ruleStr);
		}
		return queryMap;
	}

	/**
	 * 检查是否存在数据库映射
	 * @param entity
	 * @return
	 */
	private boolean instanceConfigCheck(String entity){
		String table = tableConverter.getTableName(entity);
		String id = tableConverter.getIdColumn(entity);
		return StringUtils.isNotBlank(table) && StringUtils.isNotBlank(id);
	}

	/**
	 * 当数据映射不对时
	 * 返回false的查询结果
	 * @return
	 */
	private QueryRet getQueryWithBadConfig(){
		return new QueryRet();
	}

	private boolean getBooleanWithBadConfig(){
		return false;
	}

}
