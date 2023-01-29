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
 * 预定义父类规则解释器
 * 例：predefine:dept:parent:1
 * SELECT * FROM perm_department WHERE id IN (
 *      SELECT dept.pid FROM perm_department AS dept LEFT JOIN perm_link_user_department AS link ON dept.id = link.deptid
 *      WHERE link.uid = #{IdentityManager.getPrimary()}
 *  )
 *
 * @author sobann
 */
public class PreParentRuleParser implements RuleParser {

    private Pattern preParent = Pattern.compile("^predefine:(\\w+\\-?)+:parent:\\d{1}?$");

    @Override
    public boolean match(String rule) {
        return preParent.matcher(rule).matches();
    }

    @Override
    public String parse(String[] rule, SqlTranslate sqlTranslate, String identity) {
        if (StringUtils.isBlank(identity)) {
            return null;
        }
        //predefine:dept:parent:1
        String scope = rule[1];
        //根据用户所在位置（组、空间）
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);
        //根据用户所在位置（组、空间）
        String temp_sql = sqlTranslate.buildSql(scope, "user", uids);
        //获取所在范围
        Set<String> ids = sqlTranslate.querySql(temp_sql);
        //获取父级层级
        int level = Integer.parseInt(rule[3]);
        //获取该层级的所有父级
        Collection<String> parents = sqlTranslate.queryParentIdsOfLevel(scope, ids, level);
        //再获取这些父级的所有子级
        Collection<String> childs = sqlTranslate.queryChildrenIds(scope, parents);
        return sqlTranslate.buildSelfSql(scope, childs);
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public FieldValue buildCondition(String entity, String[] rule, SqlTranslate sqlTranslate, String identity, Boolean isStr) {
        String scope = rule[1];
        //根据用户所在位置（组、空间）
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);
        //根据用户所在位置（组、空间）
        String tempSql = sqlTranslate.buildSql(scope, "user", uids);
        //获取所在范围
        Set<String> ids = sqlTranslate.querySql(tempSql);
        //获取父级层级
        int level = Integer.parseInt(rule[3]);
        //获取该层级的所有父级
        Collection<String> parents = sqlTranslate.queryParentIdsOfLevel(scope, ids, level);
        //再获取这些父级的所有子级
        Collection<String> childs = sqlTranslate.queryChildrenIds(scope, parents);
        FieldValue fieldValue = new FieldValue();
        fieldValue.setName(scope);
        fieldValue.setJsonValue(JSON.toJSONString(childs));
        fieldValue.setType(isStr ? String[].class : Long[].class);
        return fieldValue;
    }

    @Override
    public void buildQueryMap(String entity, String[] rule, List<String> scopes, List<String> props, String identity, QueryMap queryMap, SqlTranslateSelector sqlTranslateSelector) {
        String scope = rule[1];
        SqlTranslate sqlTranslate = sqlTranslateSelector.getSqlTranslate(scope, "user");
        if (sqlTranslate == null) {
            return;
        }
        //根据用户所在位置（组、空间）
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);
        //根据用户所在位置（组、空间）
        String tempSql = sqlTranslate.buildSql(scope, "user", uids);
        //获取所在范围
        Set<String> ids = sqlTranslate.querySql(tempSql);
        //获取父级层级
        int level = Integer.parseInt(rule[3]);
        //获取该层级的所有父级
        Collection<String> parents = sqlTranslate.queryParentIdsOfLevel(scope, ids, level);
        //再获取这些父级的所有子级
        Collection<String> childs = sqlTranslate.queryChildrenIds(scope, parents);
        DegradeUtils.degradeScope(scope, childs, scopes, queryMap, sqlTranslateSelector);
    }
}
