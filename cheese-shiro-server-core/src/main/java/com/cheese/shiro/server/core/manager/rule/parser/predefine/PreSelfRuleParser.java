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
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 预定义自身规则解释器，相对于child和parent的规则解释器
 * predefine:dept:self
 * SELECT dept.* FROM perm_department AS dept LEFT JOIN perm_link_user_department AS link ON dept.id = link.deptid
 * WHERE link.uid = #{IdentityManager.getPrimary()}
 *
 * @author sobann
 */
public class PreSelfRuleParser implements RuleParser {
    private Pattern preSelf = Pattern.compile("^predefine:(\\w+\\-?)+:self$");

    @Override
    public boolean match(String rule) {
        return preSelf.matcher(rule).matches();
    }

    @Override
    public String parse(String[] rule, SqlTranslate sqlTranslate, String identity) {
        if (StringUtils.isBlank(identity)) {
            return null;
        }
        String scope = rule[1];
        //根据用户所在位置（组、空间）
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);
        return sqlTranslate.buildSql(scope, "user", uids);
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public FieldValue buildCondition(String entity, String[] rule, SqlTranslate sqlTranslate, String identity, Boolean isStr) {
        String scope = rule[1];
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);

        Set<String> ids = null;
        String sql = sqlTranslate.buildSql(scope, "user", uids);
        ids = sqlTranslate.querySql(sql);

        FieldValue fieldValue = new FieldValue();
        fieldValue.setName(scope);
        fieldValue.setJsonValue(JSON.toJSONString(ids));
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
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);
        Set<String> ids = null;
        String sql = sqlTranslate.buildSql(scope, "user", uids);
        ids = sqlTranslate.querySql(sql);

        DegradeUtils.degradeScope(scope, ids, scopes, queryMap, sqlTranslateSelector);
    }
}
