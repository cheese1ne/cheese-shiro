package com.cheese.shiro.server.core.manager.rule.parser.predefine;

import com.alibaba.fastjson.JSON;
import com.cheese.shiro.common.rule.RuleParser;
import com.cheese.shiro.common.service.entity.FieldValue;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.sql.SqlTranslateSelector;
import com.cheese.shiro.server.core.manager.rule.parser.DegradeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 预定义子类规则解释器
 * 例：predefine:dept:child
 * SELECT * FROM perm_department WHERE pid IN (
 *      SELECT dept.id FROM perm_department AS dept LEFT JOIN perm_link_user_department AS link ON dept.id = link.deptid
 *      WHERE link.uid = #{IdentityManager.getPrimary()}
 *  )
 *
 * @author sobann
 */
public class PreChildRuleParser implements RuleParser {

    private Pattern preChild = Pattern.compile("^predefine:(\\w+\\-?)+:child");

    @Override
    public boolean match(String rule) {
        return preChild.matcher(rule).matches();
    }

    @Override
    public String parse(String[] rule, SqlTranslate sqlTranslate, String identity) {
        if (StringUtils.isBlank(identity)) {
            return null;
        }
        String scope = rule[1];
        //向子集扩展
        //根据用户所在位置（组、空间）
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);
        String tempSql = sqlTranslate.buildSql(scope, "user", uids);
        //获取所在范围
        Set<String> ids = sqlTranslate.querySql(tempSql);
        //获取范围子级
        Collection<String> childenIds = sqlTranslate.queryChildrenIds(scope, ids);
        return sqlTranslate.buildSelfSql(scope, childenIds);
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public FieldValue buildCondition(String entity, String[] rule, SqlTranslate sqlTranslate, String identity, Boolean isStr) {
        String scope = rule[1];
        //向子集扩展
        //根据用户所在位置（组、空间）
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);
        String tempSql = sqlTranslate.buildSql(scope, "user", uids);
        //获取所在范围
        Set<String> ids = sqlTranslate.querySql(tempSql);
        //获取范围子级
        Collection<String> childrenIds = sqlTranslate.queryChildrenIds(scope, ids);
        FieldValue fieldValue = new FieldValue();
        fieldValue.setName(scope);
        fieldValue.setJsonValue(JSON.toJSONString(childrenIds));
        fieldValue.setType(isStr ? String[].class : Long[].class);
        return fieldValue;
    }

    @Override
    public void buildQueryMap(String entity, String[] rule, List<String> scopes, List<String> props, String identity, QueryMap queryMap, SqlTranslateSelector sqlTranslateSelector) {
        String scope = rule[1];
        //向子集扩展
        //根据用户所在位置（组、空间）
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);
        SqlTranslate sqlTranslate = sqlTranslateSelector.getSqlTranslate(scope, "user");
        if (sqlTranslate == null) {
            return;
        }
        String tempSql = sqlTranslate.buildSql(scope, "user", uids);
        //获取所在范围
        Set<String> ids = sqlTranslate.querySql(tempSql);
        //获取范围子级
        Collection<String> childrenIds = sqlTranslate.queryChildrenIds(scope, ids);
        DegradeUtils.degradeScope(scope, childrenIds, scopes, queryMap, sqlTranslateSelector);
    }
}
