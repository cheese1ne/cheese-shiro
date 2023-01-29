package com.cheese.shiro.server.core.manager.instance;


import com.cheese.shiro.common.domain.SPage;
import com.cheese.shiro.common.service.entity.QueryCondition;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.service.entity.QueryRet;
import com.cheese.shiro.common.table.EntityInfo;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据实例管理器
 * 1.判断 rule 数据规则 与 instanceId 实例的包含关系
 * 2.根据 rule 数据规则，返回数据实例合集
 * 3.根据 rule 数据规则，返回数据实例条件
 * <p>
 * identity 参数 为 predefine 规则使用
 * 如rule中不含 predefine，则可为null
 * identity=null时，predefine无法解析
 *
 * @author sobann
 */
public interface InstanceManager {


    /**
     * 判断 rule 是否包含 instanceId
     *
     * @param identity   用户身份 (当rule不含predefine时，可为null)
     * @param rule       数据规则
     * @param instance   实例类型
     * @param instanceId 实例id
     * @return
     */
    boolean checkInstance(String identity, String rule, String instance, String instanceId);


    /**
     * 判断 rules 是否全部包含 instanceIds
     *
     * @param identity    用户身份  (当rule不含predefine时，可为null)
     * @param rules       数据规则
     * @param instance    实例类型
     * @param instanceIds 实例id
     * @return
     */
    boolean checkInstance(String identity, Set<String> rules, String instance, Collection<String> instanceIds);


    /**
     * 将 rule 规则 转换为 entity 的实例集合
     *
     * @param identity 用户身份  (当rule不含predefine时，可为null)
     * @param entity   实例类型 （entity 为规则指定的最初、原子类型）
     * @param rules    数据规则
     * @return
     */
    QueryRet getInstanceIds(String identity, String entity, Collection<String> rules);

    /**
     * 将 rule 规则转换为 scope 的 实例结核
     *
     * @param identity    用户身份  (当rule不含predefine时，可为null)
     * @param scope       范围类型（scope 不是规则执行的原子类型，原子类型向上的一个范围类型）
     * @param rules       数据规则
     * @param ignoreLevel 转换时，是否执行 子范围转换为符范围
     * @return
     */
    QueryRet getScopeIds(String identity, String scope, Collection<String> rules, boolean ignoreLevel);

    /**
     * 根据 结果集，获取对应 实体实例 的集合
     *
     * @param target   实体类型
     * @param queryRet 结果集
     * @param keyword  关键词
     * @return
     */
    List<EntityInfo> getEntityInfo(String target, QueryRet queryRet, String keyword);

    /**
     * 根据 结果集，获取队形实体实例 的 分页 结果
     *
     * @param target   实体类型
     * @param queryRet 结果集
     * @param keyword  关键词
     * @param page     页码
     * @param size     行数
     * @return
     */
    SPage<EntityInfo> getPageOfEntityInfo(String target, QueryRet queryRet, String keyword, int page, int size);

    /**
     * 获取对应实体的实例的 id 树形列表
     *
     * @param entity 实体类型
     * @return
     */
    Map<String, String> getTreeIdsOfEntity(String entity);

    /**
     * 获取该数据规则对应的 实例集合
     *
     * @param identity 用户身份
     * @param rule     数据规则
     * @return
     */
    QueryRet getScopeIdsOnlyForRule(String identity, String rule);

    /**
     * 获取 rule 对应的 实体 的条件集合（es使用）
     *
     * @param identity 用户身份  (当rule不含predefine时，可为null)
     * @param entity   实体类型 （最初原子实体类型）
     * @param rules    数据规则
     * @return
     */
    QueryCondition getConditions(String identity, String entity, Collection<String> rules);

    /**
     * 获取 rule 对应的 实体范围 查询集合（@MultipleAuthKey）
     * entity的所有规则，向scopes 依次靠拢转换
     *
     * @param identity 用户身份  (当rule不含predefine时，可为null)
     * @param entity   原子实体类型
     * @param scopes   转换成的 实例类型
     * @param rules    数据规则
     * @return
     */
    QueryMap getQueryMap(String identity, String entity, String[] scopes, Collection<String> rules);
}