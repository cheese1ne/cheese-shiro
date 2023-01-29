package com.cheese.shiro.server.core.manager.rule;


import com.cheese.shiro.common.rule.RuleParser;
import com.cheese.shiro.common.service.entity.FieldValue;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.sql.SqlTranslateSelector;
import com.cheese.shiro.common.table.TableConverter;
import com.cheese.shiro.common.table.TableLink;
import com.cheese.shiro.server.core.manager.rule.parser.RuleParserManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * 数据规则解释器
 * 解析规则后，使用sqltranslate 拼写为sql
 *
 * @author ll
 */

public class RuleExplainer {
    private static final Logger logger = LoggerFactory.getLogger(RuleExplainer.class);
    public final static String One = "one";
    public final static String Separator = ":";
    public final static String All = "*";
    public final static String Default = "_";
    public final static String Addition = "&";

    private SqlTranslateSelector sqlTranslateSelector;
    private TableConverter tableConverter;
    private RuleParserManager ruleParserManager;

    public RuleExplainer(SqlTranslateSelector sqlTranslateSelector, TableConverter tableConverter, RuleParserManager ruleParserManager) {
        this.sqlTranslateSelector = sqlTranslateSelector;
        this.tableConverter = tableConverter;
        this.ruleParserManager = ruleParserManager;
    }

    public SqlTranslate getSqlTranslate(String entity) {
        return sqlTranslateSelector.getSqlTranslate(entity);
    }

    public SqlTranslate getSqlTranslate(String target, String scope) {
        if (target.equalsIgnoreCase(scope)) {
            return getSqlTranslate(target);
        }
        return sqlTranslateSelector.getSqlTranslate(target, scope);
    }

    public String getScopeFromRule(String rule) {
        //group
        return rule.split(Separator)[1];
    }

    public SqlTranslate getSqlTranslateByScopeAndRule(String operateScope, String rule) {
        String scope = getScopeFromRule(rule);
        return getSqlTranslate(operateScope, scope);
    }


    public String buildSqlForRule(String identity, String rule) {
        String[] rules = rule.split(Separator);
        String scope = rules[1];
        SqlTranslate scopeSqlTranslate = getSqlTranslate(scope);
        RuleParser ruleParser = ruleParserManager.matchParser(rule);
        if (ruleParser == null) {
            return null;
        }
        return ruleParser.parse(rules, scopeSqlTranslate, identity);
    }


    /**
     * 数据规则检查使用，true/false比较
     * rule 必须 可转换为 operateScope，可能返回sql
     * 1.rule 中 scope 与 operateScope 存在直接联系
     * 2.rule 中 level 比 operateScope 高，即父范围 转换为子范围
     *
     * @param identity
     * @param operateScope 操作对象
     * @param hasRule      数据规则
     * @return
     */
    @SuppressWarnings("unchecked")
    public String buildSqlForCheckInstance(String identity, String operateScope, String hasRule) {
        //predefine:group:self
        String[] rules = hasRule.split(Separator);
        //group
        String scope = rules[1];
        //当操作范围与已有范围不相同，且无法进行转化时，权限失效
        if (!operateScope.equals(scope)) {
            //所操作的范围与已有范围无法转化，权限无效
            TableLink tableLink = tableConverter.getTableLink(operateScope, scope);
            if (tableLink == null) {
                return null;
            }
        }

        //获取范围id的sql语句
        String scopeSql;
        //进行规则匹配和解析
        RuleParser ruleParser = ruleParserManager.matchParser(hasRule);
        if (ruleParser == null) {
            return null;
        }
        SqlTranslate scopeSqlTranslate = getSqlTranslate(scope);
        scopeSql = ruleParser.parse(rules, scopeSqlTranslate, identity);
        //解析出现错误
        if (StringUtils.isBlank(scopeSql)) {
            return null;
        }

        String sql = null;
        SqlTranslate linkSqlTranslate = getSqlTranslate(operateScope, scope);
        //操作范围与权限范围相同
        if (operateScope.equalsIgnoreCase(scope)) {
            sql = scopeSql;
        } else {
            //如果 权限范围可向操作范围转化
            if (linkSqlTranslate.isContainer(operateScope, scope)) {
                //如果是同一实现
                if (linkSqlTranslate.equals(scopeSqlTranslate)) {
                    sql = linkSqlTranslate.buildSubSql(operateScope, scope, scopeSql);
                } else {
                    //不是同一实现
                    Set<String> scopeIds = scopeSqlTranslate.querySql(scopeSql);
                    sql = linkSqlTranslate.buildSql(operateScope, scope, scopeIds);
                }
            } else {
                //不可转化，则无sql可以查询
                sql = null;
            }
        }
        return sql;
    }

    /**
     * 数据规则向 operateScope 转换使用，scope 查询使用，用于汇总规则中所包含的数据范围
     * 1.ignoreLevel =true;
     * 只要存在联系，即可构建sql
     * 2.ignoreLevel = false;
     * (1) 存在转换联系
     * (1) 父范围 -> 子范围
     *
     * @param identity
     * @param operateScope 操作（转换）对象
     * @param hasRule
     * @param ignoreLevel  是否忽视包含关系，即子范围向父范围转换
     * @return
     */
    @SuppressWarnings("unchecked")
    public String buildSqlForQueryScope(String identity, String operateScope, String hasRule, boolean ignoreLevel) {

        //hash:group:self  type=self,group=scope
        String[] rule = hasRule.split(Separator);
        //String type = rule[0];
        String scope = rule[1];

        //当操作范围与已有范围不相同且没有关联时，无法进行转化，只能作为附加条件
        if (!operateScope.equals(scope)) {
            //所操作的范围与已有范围无法进行关联，即权限无法制约该范围,只能扩展为全范围，不做限制
            TableLink tableLink = tableConverter.getTableLink(operateScope, scope);
            if (tableLink == null) {
                return Addition;
            }
        }


        //获取范围id的sql语句
        //如果不考虑 操作范围大小 或者，权限范围大于操作范围
        if (!ignoreLevel) {
            //获取解析器
            boolean container = getSqlTranslate(operateScope, scope).isContainer(operateScope, scope);
            //当查询范围比权限范围大时，不进行解析
            if (!container) {
                return null;
            }
        }


        String scopeSql;
        //进行规则匹配和解析
        RuleParser ruleParser = ruleParserManager.matchParser(hasRule);
        if (ruleParser == null) {
            return null;
        }
        SqlTranslate scopeSqlTranslate = getSqlTranslate(scope);
        scopeSql = ruleParser.parse(rule, scopeSqlTranslate, identity);
        if (StringUtils.isBlank(scopeSql)) {
            return null;
        }

        String sql = null;
        if (operateScope.equalsIgnoreCase(scope)) {
            sql = scopeSql;
        } else {
            SqlTranslate linkSqlTranslate = getSqlTranslate(operateScope, scope);
            //如果是同一实现
            if (linkSqlTranslate.equals(scopeSqlTranslate)) {
                sql = linkSqlTranslate.buildSubSql(operateScope, scope, scopeSql);
            } else {
                //不是同一实现
                Set<String> scopeIds = scopeSqlTranslate.querySql(scopeSql);
                sql = linkSqlTranslate.buildSql(operateScope, scope, scopeIds);
            }
        }
        return sql;
    }


    public FieldValue buildConditions(String entity, String identity, String rule) {
        RuleParser ruleParser = ruleParserManager.matchParser(rule);
        if (ruleParser == null) {
            return null;
        }
        String[] rules = rule.split(Separator);
        String scope = rules[1];
        Boolean isStr = tableConverter.idTypeIsStr(scope);
        SqlTranslate sqlTranslate = getSqlTranslate(scope);
        return ruleParser.buildCondition(entity, rules, sqlTranslate, identity, isStr);
    }

    public void buildQueryMap(String entity, String identity, String rule, List<String> scopes, List<String> props, QueryMap queryMap) {
        RuleParser ruleParser = ruleParserManager.matchParser(rule);
        if (ruleParser != null) {
            String[] rules = rule.split(Separator);
            ruleParser.buildQueryMap(entity, rules, scopes, props, identity, queryMap, sqlTranslateSelector);
        }
    }
}
