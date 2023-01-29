package com.cheese.shiro.common.sql;


import com.cheese.shiro.common.domain.SPage;
import com.cheese.shiro.common.table.EntityInfo;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 数据转换器，用于查询实体信息或构建权限Sql
 *
 * @author sobann
 */
public interface SqlTranslate {
    /**
     * 返回 能够查询到 与 target 关联 的 scopeId 的 sql语句
     *
     * @param target
     * @param scope
     * @param scopeIds
     * @return
     */
    String buildSql(String target, String scope, Collection<? extends Serializable> scopeIds);

	/**
	 * 构建能够查询到 与 target 关联 的 scopeId 的 sql语句，且不向子集拓展
	 * @param target
	 * @param targetIds
	 * @return
	 */
	String buildSelfSql(String target, Collection<? extends Serializable> targetIds);

    /**
     * 返回目标的 子级ids
     *
     * @param target
     * @param targetIds
     * @return
     */
    Collection<String> queryChildrenIds(String target, Collection<String> targetIds);

    /**
     * 返回满足目标的 条件的 id
     *
     * @param target
     * @param condition
     * @return
     */
    String buildSqlByCondition(String target, String condition);

    /**
     * 判断范围大小，大范围内应包含小范围
     *
     * @param object    物体
     * @param container 容器
     * @return
     */
    boolean isContainer(String object, String container);

    /**
     * scope在子查询范围内 获取 targetIds 的sql 语句
     *
     * @param target
     * @param scope
     * @param subQuery 子查询语句
     * @return
     */
    String buildSubSql(String target, String scope, String subQuery);

    /**
     * 执行sql，返回查询结果，ids集合
     *
     * @param sql
     * @return
     */
    Set<String> querySql(String sql);

    /**
     * instanceId 是否在 sql查询结果的集合中
     *
     * @param sql
     * @param instanceId
     * @return
     */
    boolean isResultContainsInstace(String sql, String instanceId);

    /**
     * 查询 实体基本信息
     * 当isAll=true时，返回全部结果，此时targetIds为空
     *
     * @param target    对象目标
     * @param targetIds 对象目标的id
     * @param isAll     查否查询全部结果
     * @param keyword   关键字
     * @return
     */

    List<EntityInfo> queryEntityInfo(String target, List<String> targetIds, boolean isAll, String keyword);

    /**
     * 分页查询基本信息
     *
     * @param target
     * @param targetIds
     * @param isAll
     * @param page
     * @param size
     * @param keyword
     * @return
     */
    SPage<EntityInfo> queryPageOfEntityInfo(String target, List<String> targetIds, boolean isAll, String keyword, int page, int size);

    /**
     * 返回能够创建者所创建的数据sql
     * select  targetid from target_table where createby in [uids]
     *
     * @param target
     * @param createIds
     * @return
     */
    String buildCreateSql(String target, Collection<? extends Serializable> createIds);

    /**
     * 查询target对象的级别为level的id
     *
     * @param target
     * @param targetIds
     * @param level
     * @return
     */
    Collection<String> queryParentIdsOfLevel(String target, Collection<String> targetIds, int level);

    /**
     * 查询 target对象 的id->pid 树形关系
     *
     * @param target
     * @return
     */
    Map<String, String> queryTreeIdsMap(String target);

}
