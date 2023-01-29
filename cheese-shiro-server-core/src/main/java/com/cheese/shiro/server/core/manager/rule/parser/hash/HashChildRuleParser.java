package com.cheese.shiro.server.core.manager.rule.parser.hash;

import com.alibaba.fastjson.JSON;
import com.cheese.shiro.common.rule.RuleParser;
import com.cheese.shiro.common.service.entity.FieldValue;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.sql.SqlTranslateSelector;
import com.cheese.shiro.server.core.manager.rule.parser.DegradeUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 对于已定义哈希规则实体子类的规则进行解析，配置时要参考已创建数据主键列
 * 例如：hash:dept:child:2,3,10,11,14
 * 获取拥有子部门为(2,3,10,11,14)的部门
 * SELECT * FROM perm_department WHERE id IN (
 *      SELECT pid FROM perm_department WHERE id IN (2,3,10,11,14)
 * )
 *
 * @author sobann
 * @date 2018/12/17
 */
public class HashChildRuleParser implements RuleParser {

    private Pattern hashChild = Pattern.compile("^hash:(\\w+\\-?)+:child:.+");

    @Override
    public boolean match(String rule) {
        return hashChild.matcher(rule).matches();
    }

    @Override
    public String parse(String[] rule, SqlTranslate sqlTranslate, String identity) {
        String scope = rule[1];
        String[] ids = rule[3].split(",");
        List<String> parentIds = Arrays.asList(ids);
        Collection<String> scopeIds = sqlTranslate.queryChildrenIds(scope, parentIds);
        return sqlTranslate.buildSelfSql(scope, scopeIds);
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public FieldValue buildCondition(String entity, String[] rule, SqlTranslate sqlTranslate, String identity, Boolean isStr) {
        String scope = rule[1];
        String[] ids = rule[3].split(",");
        List<String> parentIds = Arrays.asList(ids);
        Collection<String> scopeIds = sqlTranslate.queryChildrenIds(scope, parentIds);
        FieldValue fieldValue = new FieldValue();
        fieldValue.setName(scope);
        fieldValue.setJsonValue(JSON.toJSONString(scopeIds));
        fieldValue.setType(isStr ? String[].class : Long[].class);
        return null;
    }

    @Override
    public void buildQueryMap(String entity, String[] rule, List<String> scopes, List<String> props, String identity, QueryMap queryMap, SqlTranslateSelector sqlTranslateSelector) {
        String scope = rule[1];
        String[] ids = rule[3].split(",");
        SqlTranslate sqlTranslate = sqlTranslateSelector.getSqlTranslate(scope);
        if (sqlTranslate == null) {
            return;
        }
        List<String> parentIds = Arrays.asList(ids);
        Collection<String> scopeIds = sqlTranslate.queryChildrenIds(scope, parentIds);
        DegradeUtils.degradeScope(scope, scopeIds, scopes, queryMap, sqlTranslateSelector);
    }


}
