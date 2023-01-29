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
 * 对 hash:dept:self:2,3,10,11,14 进行解析
 * SELECT * FROM perm_department WHERE id IN (2,3,10,11,14)
 *
 * @author sobann
 * @date 2018/12/17
 */
public class HashSelfRuleParser implements RuleParser {

    private Pattern hashSelf = Pattern.compile("^hash:(\\w+\\-?)+:self:.+");

    @Override
    public boolean match(String rule) {
        return hashSelf.matcher(rule).matches();
    }

    @Override
    public String parse(String[] rule, SqlTranslate sqlTranslate, String identity) {
        String[] ids = rule[3].split(",");
        Collection<String> scopeIds = Arrays.asList(ids);
        String scope = rule[1];
        return sqlTranslate.buildSelfSql(scope, scopeIds);
    }

    @Override
    public int order() {
        return 1;
    }

    @Override
    public FieldValue buildCondition(String entity, String[] rule, SqlTranslate sqlTranslate, String identity, Boolean isStr) {
        String ids = rule[3];
        String scope = rule[1];
        FieldValue fieldValue = new FieldValue();
        fieldValue.setName(scope);
        fieldValue.setJsonValue(JSON.toJSONString(ids.split(",")));
        fieldValue.setType(isStr ? String[].class : Long[].class);

        return fieldValue;
    }

    @Override
    public void buildQueryMap(String entity, String[] rule, List<String> scopes, List<String> props, String identity, QueryMap queryMap, SqlTranslateSelector sqlTranslateSelector) {
        String scope = rule[1];
        List<String> scopeIds = Arrays.asList(rule[3].split(","));
        DegradeUtils.degradeScope(scope, scopeIds, scopes, queryMap, sqlTranslateSelector);
    }
}
