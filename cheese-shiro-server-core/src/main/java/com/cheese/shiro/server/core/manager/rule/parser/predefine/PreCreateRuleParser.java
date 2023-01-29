package com.cheese.shiro.server.core.manager.rule.parser.predefine;

import com.alibaba.fastjson.JSON;
import com.cheese.shiro.common.rule.RuleParser;
import com.cheese.shiro.common.service.entity.FieldValue;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.sql.SqlTranslateSelector;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * 预定义创建规则解释器，使用较多，例如只有项目创建者具有的权限操作
 * 例：predefine:dept:create
 * SELECT * FROM perm_department WHERE create_user = #{IdentityManager.getPrimary()}
 *
 * @author sobann
 */
public class PreCreateRuleParser implements RuleParser {

    private Pattern preCreate = Pattern.compile("^predefine:(\\w+\\-?)+:create$");

    @Override
    public boolean match(String rule) {
        return preCreate.matcher(rule).matches();
    }

    @Override
    public String parse(String[] rule, SqlTranslate sqlTranslate, String identity) {
        if (StringUtils.isBlank(identity)) {
            return null;
        }
        //predefine:dept:create
        String scope = rule[1];
        List<String> uids = new ArrayList<>(1);
        uids.add(identity);
        return sqlTranslate.buildCreateSql(scope, uids);
    }

    @Override
    public int order() {
        return 0;
    }

    @Override
    public FieldValue buildCondition(String entity, String[] rule, SqlTranslate sqlTranslate, String identity, Boolean isStr) {
        String scope = rule[1];
        List<String> uids = new ArrayList<>();
        uids.add(identity);
        if (entity.equalsIgnoreCase(scope)) {
            return new FieldValue("create", JSON.toJSONString(uids), Long[].class);
        } else {
            String sql = sqlTranslate.buildCreateSql(scope, uids);
            Set<String> set = sqlTranslate.querySql(sql);
            FieldValue fieldValue = new FieldValue();
            fieldValue.setName(scope);
            fieldValue.setJsonValue(JSON.toJSONString(set));
            fieldValue.setType(isStr ? String[].class : Long[].class);
            return fieldValue;
        }

    }

    @Override
    public void buildQueryMap(String entity, String[] rule, List<String> scopes, List<String> props, String identity, QueryMap queryMap, SqlTranslateSelector sqlTranslateSelector) {
    }
}
