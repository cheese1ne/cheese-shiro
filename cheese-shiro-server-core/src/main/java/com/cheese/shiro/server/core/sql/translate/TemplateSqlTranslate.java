package com.cheese.shiro.server.core.sql.translate;


import com.cheese.shiro.common.domain.SPage;
import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.table.EntityInfo;
import com.cheese.shiro.common.table.TableConverter;
import com.cheese.shiro.common.table.TableLink;
import com.cheese.shiro.common.util.SqlUtil;
import com.cheese.shiro.common.util.TreeUtils;
import com.cheese.shiro.server.core.sql.StmtExecutor;
import net.sf.jsqlparser.JSQLParserException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据转换器默认实现
 * 内部包含sql执行器以及实体转换器
 *
 * @author sobann
 */
public class TemplateSqlTranslate implements SqlTranslate {

    private static final Logger logger = LoggerFactory.getLogger(TemplateSqlTranslate.class);
    protected TableConverter tableConverter;
    protected StmtExecutor stmtExecutor;

    public TemplateSqlTranslate(TableConverter tableConverter, StmtExecutor stmtExecutor) {
        this.tableConverter = tableConverter;
        this.stmtExecutor = stmtExecutor;
    }

    @Override
    public String buildSql(String target, String scope, Collection<? extends Serializable> scopeIds) {
        if (CollectionUtils.isEmpty(scopeIds)) {
            logger.info("Collection is Entity");
            return null;
        }
        Link link = getLinkInfo(target, scope);
        String targetId = link.getTargetId();
        String scopeId = link.getScopeId();
        String linkTargetScope = link.getLinkTable();
        Boolean isStr = tableConverter.idTypeIsStr(scope);
        String sql = "SELECT " + targetId + " FROM " + linkTargetScope;
        String newSql = null;
        try {
            newSql = SqlUtil.addInCondition(sql, scopeId, scopeIds, isStr);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        logger.info("buildSql:{}", newSql);
        return newSql;
    }

    /**
     * 根据 ids 组建查询语句
     *
     * @param target
     * @param targetIds
     * @return
     */
    @Override
    public String buildSelfSql(String target, Collection<? extends Serializable> targetIds) {
        if (CollectionUtils.isEmpty(targetIds)) {
            logger.info("Collection is Entity");
            return null;
        }
        String targetId = tableConverter.getIdColumn(target);
        String targetTable = tableConverter.getTableName(target);
        Boolean isStr = tableConverter.idTypeIsStr(target);
        String sql = "SELECT " + targetId + " FROM " + targetTable;
        String newSql = null;
        try {
            newSql = SqlUtil.addInCondition(sql, targetId, targetIds, isStr);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        logger.info("buildSelfSql:{}", newSql);
        return newSql;
    }

    /**
     * 根据目标ids，查询所有子级id，包含当前查询ids
     *
     * @param target
     * @param targetIds
     * @return 未找到子级配置，将仅返回本身
     */
    @Override
    public Collection<String> queryChildrenIds(String target, Collection<String> targetIds) {
        if (CollectionUtils.isEmpty(targetIds)) {
            logger.info("Collection is Entity");
            return new HashSet<>();
        }
        Map<String, String> tree = queryTreeIdsMap(target);
        if (CollectionUtils.isEmpty(tree)) {
            logger.info("Can Not Tree Ids Map Of {},Return TargetIds Back", target);
            return targetIds;
        }
        List<String> childs = TreeUtils.getChildrenList(targetIds, tree);
        childs.addAll(targetIds);
        return childs;
    }

    /**
     * 根据sql条件，组件查询sql
     *
     * @param target
     * @param condition
     * @return
     */
    @Override
    public String buildSqlByCondition(String target, String condition) {
        String targetId = tableConverter.getIdColumn(target);
        String tableName = tableConverter.getTableName(target);
        String sql = "SELECT " + targetId + " FROM " + tableName;
        String newSql = null;
        try {
            newSql = SqlUtil.addCondition(sql, condition);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        logger.info("buildSqlByCondition:{}", newSql);
        return newSql;
    }

    /**
     * 范围比较
     *
     * @param object    物体
     * @param container 容器
     * @return
     */
    @Override
    public boolean isContainer(String object, String container) {
        int o = tableConverter.getLevel(object);
        int c = tableConverter.getLevel(container);
        return c >= o;
    }

    /**
     * 根据子查询语句，组建sql
     *
     * @param target
     * @param scope
     * @param subQuery 子查询语句
     * @return
     */
    @Override
    public String buildSubSql(String target, String scope, String subQuery) {
        if (target.equalsIgnoreCase(scope)) {
            return subQuery;
        }
        Link link = getLinkInfo(target, scope);
        String targetId = link.getTargetId();
        String scopeId = link.getScopeId();
        String linkTable = link.getLinkTable();
        String sql = "SELECT " + targetId + " FROM " + linkTable;
        String newSql = null;
        try {
            newSql = SqlUtil.addSubSqlCondition(sql, scopeId, subQuery);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        logger.info("buildSql:{}", newSql);
        return newSql;
    }


    /**
     * 进行语句查询，返回ids
     *
     * @param stmt
     * @return
     */
    @Override
    public Set<String> querySql(String stmt) {
        return stmtExecutor.executeSqlForIds(stmt, null);
    }

    /**
     * 判断sql查询结果集，是否包含该id
     *
     * @param sql
     * @param instanceId
     * @return
     */
    @Override
    public boolean isResultContainsInstace(String sql, String instanceId) {
        try {
            String s = SqlUtil.addResultCondition(sql, instanceId);
            Set<String> set = querySql(s);
            if (CollectionUtils.isEmpty(set)) {
                return false;
            } else {
                return true;
            }
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据id或者全部查询，返回目标集的基本信息
     *
     * @param target    对象目标
     * @param targetIds 对象目标的id
     * @param isAll     查否查询全部结果
     * @return
     */
    @Override
    public List<EntityInfo> queryEntityInfo(String target, List<String> targetIds, boolean isAll, String keyword) {
        if (CollectionUtils.isEmpty(targetIds) && !isAll) {
            logger.info("Collection is Entity");
            return new ArrayList<>();
        }
        List<Object> params = new ArrayList<>();
        String sql = getEntityQuerySql(target, targetIds, isAll, keyword, params);
        return stmtExecutor.getEntityInfoFromSql(sql, params.toArray());
    }

    public String getEntityQuerySql(String target, List<String> targetIds, boolean isAll, String keyword, List<Object> params) {
        String targetId = tableConverter.getIdColumn(target);
        String targetName = tableConverter.getNameColumn(target);
        String targetTable = tableConverter.getTableName(target);
        String targetPid = tableConverter.getPidColumn(target);
        String props = tableConverter.getPropsColumn(target);
        String propsColumn = "";
        if (StringUtils.isNotBlank(props)) {
            propsColumn = ", " + props;
        }
        String sql = "";
        if (StringUtils.isEmpty(targetPid)) {
            sql = "SELECT " + targetId + " AS id," + targetName + " AS name" + propsColumn + " FROM " + targetTable;
        } else {
            sql = "SELECT " + targetId + " AS id," + targetName + " AS name," + targetPid + " AS pid " + propsColumn + "  FROM " + targetTable;
        }

        if (StringUtils.isNotBlank(keyword)) {
            String condition = targetId + " = ? OR " + targetName + " LIKE CONCAT('%',?,'%')";
            params.add(keyword);
            params.add(keyword);
            try {
                sql = SqlUtil.addCondition(sql, condition);
            } catch (JSQLParserException e) {
                e.printStackTrace();
            }
        }

        if (!isAll) {
            Boolean isStr = tableConverter.idTypeIsStr(target);
            try {
                sql = SqlUtil.addInCondition(sql, targetId, targetIds, isStr);
            } catch (JSQLParserException e) {
                e.printStackTrace();
            }
        }
        return sql;
    }


    @Override
    public SPage<EntityInfo> queryPageOfEntityInfo(String target, List<String> targetIds, boolean isAll, String keyword, int page, int size) {
        if (CollectionUtils.isEmpty(targetIds) && !isAll) {
            logger.info("Collection is Entity");
            return new SPage<>(page, size, 0, new ArrayList<>());
        }
        List<Object> params = new ArrayList<>();
        String sql = getEntityQuerySql(target, targetIds, isAll, keyword, params);
        return stmtExecutor.getPageOfEntityInfo(sql, params.toArray(), page, size);
    }

    /**
     * 根据createUser条件，组建对应查询sql
     *
     * @param target
     * @param createIds
     * @return
     */
    @Override
    public String buildCreateSql(String target, Collection<? extends Serializable> createIds) {
        if (CollectionUtils.isEmpty(createIds)) {
            logger.info("Collection is Entity");
            return null;
        }
        String createColumn = tableConverter.getCreateColumn(target);
        if (StringUtils.isBlank(createColumn)) {
            logger.info("{} createColumn is Entity", target);
            return null;
        }
        Boolean isStr = tableConverter.idTypeIsStr("user");
        String targetId = tableConverter.getIdColumn(target);
        String targetTable = tableConverter.getTableName(target);
        String sql = "SELECT " + targetId + " FROM " + targetTable;
        String newSql = null;
        try {
            newSql = SqlUtil.addInCondition(sql, createColumn, createIds, isStr);
        } catch (JSQLParserException e) {
            e.printStackTrace();
        }
        logger.info("buildSelfSql:{}", newSql);
        return newSql;
    }

    /**
     * 根据目标ids,返回对应级别父ids
     *
     * @param target
     * @param targetIds
     * @param level     从1开始，level--对应数组中index
     * @return 未找到父级配置，将返回本身
     */
    @Override
    public Collection<String> queryParentIdsOfLevel(String target, Collection<String> targetIds, int level) {
        if (level != 0) {
            level--;
        }

        if (CollectionUtils.isEmpty(targetIds)) {
            return new HashSet<>();
        }
        Map<String, String> tree = queryTreeIdsMap(target);
        if (CollectionUtils.isEmpty(tree)) {
            logger.info("Can Not Tree Ids Map Of {},Return TargetIds Back", target);
            return targetIds;
        }
        String rootId = tableConverter.getRootId(target);
        Set<String> set = new HashSet<>();
        for (String targetId : targetIds) {
            String parent = TreeUtils.getParent(targetId, tree, rootId, level);
            if (StringUtils.isNotBlank(parent)) {
                set.add(parent);
            } else {
                set.add(targetId);
            }
        }
        return set;
    }

    @Override
    public Map<String, String> queryTreeIdsMap(String target) {
        String pid = tableConverter.getPidColumn(target);
        if (StringUtils.isBlank(pid)) {
            logger.info("Can Not Find Parent Column Of {}", target);
            return new HashMap<>();
        }
        String id = tableConverter.getIdColumn(target);
        String table = tableConverter.getTableName(target);
        String sql = "SELECT " + id + " AS id," + pid + " AS pid FROM " + table;
        Map<String, String> tree = new HashMap<>();
        List<EntityInfo> list = stmtExecutor.getEntityInfoFromSql(sql, null);
        if (CollectionUtils.isEmpty(list)) {
            return tree;
        }
        return list.stream().collect(Collectors.toMap(EntityInfo::getId, EntityInfo::getPid));
    }


    private Link getLinkInfo(String target, String scope) {
        String targetId = null;
        String scopeId = null;
        String linkTable = tableConverter.getTableName(target, scope);
        if (target.equalsIgnoreCase(scope)) {
            targetId = tableConverter.getIdColumn(target);
            scopeId = targetId;
        } else {
            TableLink tableLink = tableConverter.getTableLink(target, scope);
            if (target.equalsIgnoreCase(tableLink.getEntity())) {
                targetId = tableLink.getEntityKey();
                scopeId = tableLink.getScopeKey();
            } else {
                targetId = tableLink.getScopeKey();
                scopeId = tableLink.getEntityKey();
            }
        }
        if (StringUtils.isBlank(targetId)) {
            targetId = tableConverter.getIdColumn(target);
        }
        if (StringUtils.isBlank(scopeId)) {
            scopeId = tableConverter.getIdColumn(scope);
        }
        Link link = new Link();
        link.setScopeId(scopeId);
        link.setTargetId(targetId);
        link.setLinkTable(linkTable);
        return link;
    }


    public class Link {
        private String targetId;
        private String scopeId;
        private String linkTable;

        public String getTargetId() {
            return targetId;
        }

        public void setTargetId(String targetId) {
            this.targetId = targetId;
        }

        public String getScopeId() {
            return scopeId;
        }

        public void setScopeId(String scopeId) {
            this.scopeId = scopeId;
        }

        public String getLinkTable() {
            return linkTable;
        }

        public void setLinkTable(String linkTable) {
            this.linkTable = linkTable;
        }
    }
}
