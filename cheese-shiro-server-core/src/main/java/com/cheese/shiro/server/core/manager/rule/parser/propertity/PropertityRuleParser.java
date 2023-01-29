package com.cheese.shiro.server.core.manager.rule.parser.propertity;

import com.alibaba.fastjson.JSON;
import com.cheese.shiro.common.rule.RuleParser;
import com.cheese.shiro.common.service.entity.FieldValue;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.sql.SqlTranslateSelector;
import com.cheese.shiro.common.util.ExpressUtils;
import com.cheese.shiro.server.core.manager.rule.parser.DegradeUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 实体属性规则解释器
 * 例：propertity:user:age>5
 * SELECT * FROM perm_user WHERE age > 5
 *
 * @author sobann
 */
public class PropertityRuleParser implements RuleParser {

    private Pattern proRule =Pattern.compile("^propertity:(\\w+\\-?)+:.*$");

    private List<Character> charTokens;

    public PropertityRuleParser() {
        List<Character> characters = new ArrayList<>();
        characters.add('(');
        characters.add(')');
        characters.add('=');
        characters.add('<');
        characters.add('>');
        characters.add('+');
        characters.add('-');
        characters.add('`');
        characters.add(' ');
        this.charTokens = characters;
    }

    @Override
    public boolean match(String rule) {
        return proRule.matcher(rule).matches();
    }

    @Override
    public String parse(String[] rule, SqlTranslate sqlTranslate, String identity) {
        String scope = rule[1];
        return  sqlTranslate.buildSqlByCondition(scope, rule[2]);
    }

    @Override
    public FieldValue buildCondition(String entity, String[] rule, SqlTranslate sqlTranslate, String identity, Boolean isStr) {
        String scope = rule[1];
        String condition = rule[2];
        if(scope.equalsIgnoreCase(entity)){
            return new FieldValue(scope,JSON.toJSONString(condition), String.class);
        }else{
            String sql = sqlTranslate.buildSqlByCondition(scope, condition);
            Set<String> set = sqlTranslate.querySql(sql);
            FieldValue fieldValue = new FieldValue();
            fieldValue.setName(scope);
            fieldValue.setJsonValue(JSON.toJSONString(set));
            fieldValue.setType( isStr ? String[].class : Long[].class);
            return fieldValue;
        }

    }

    @Override
    public void buildQueryMap(String entity, String[] rule, List<String> scopes, List<String> props, String identity, QueryMap queryMap, SqlTranslateSelector sqlTranslateSelector) {
        String scope = rule[1];
        String condition = rule[2];
        String prop = getProp(condition, props);
        //直接为entity属性,且在转换范围之内
        if(entity.equalsIgnoreCase(scope) && StringUtils.isNotBlank(prop) && scopes.contains(entity)){
            queryMap.addProp(prop,condition);
        }else {
            //为entity的其他属性，或者范围属性
            //将范围属性转换为id
            SqlTranslate sqlTranslate = sqlTranslateSelector.getSqlTranslate(entity);
            String sql = sqlTranslate.buildSqlByCondition(scope, condition);
            Set<String> ids = sqlTranslate.querySql(sql);
            //进行范围适配
            DegradeUtils.degradeScope(scope,ids,scopes,queryMap,sqlTranslateSelector);
        }

    }

    @Override
    public int order() {
        return 2;
    }

    /**
     * 获取表达式中属性
     * @param expess
     * @param props
     * @return
     */
    public String getProp(String expess,List<String> props){
        List<String> vars = ExpressUtils.getVars(expess);
        for (String var : vars) {
            if(props.contains(var)){
                return var;
            }
        }
        return null;
    }

}
