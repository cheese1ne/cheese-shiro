package com.cheese.shiro.common.rule;


import com.cheese.shiro.common.service.entity.FieldValue;
import com.cheese.shiro.common.service.entity.QueryMap;
import com.cheese.shiro.common.sql.SqlTranslate;
import com.cheese.shiro.common.sql.SqlTranslateSelector;

import java.util.List;

/**
 * 规则解析接口
 *
 * @author sobann
 */
public interface RuleParser {
    /**
     * 规则是否匹配
     *
     * @param rule predefine:dept:self
     * @return boolean
     */
    boolean match(String rule);

    /**
     * 解析规则
     *
     * @param rule         分割后的规则 [predefine,dept,self]
     * @param sqlTranslate sql解析器
     * @param identity     当前用户身份uid （predefine使用）
     * @return 返回值为null 或者 空字符串时 ，该条件作废
     */
    String parse(String[] rule, SqlTranslate sqlTranslate, String identity);

    /**
     * 顺序接口
     *
     * @return
     */
    int order();

    /**
     * 构建条件
     *
     * @param entity
     * @param rule         规则
     * @param sqlTranslate
     * @param isStr
     * @param identity
     * @return
     */
    FieldValue buildCondition(String entity, String[] rule, SqlTranslate sqlTranslate, String identity, Boolean isStr);

    /**
     * 构建查询条件，结果存储在参数queryMap中
     * @param entity
     * @param rule
     * @param scopes
     * @param props
     * @param identity
     * @param queryMap
     * @param sqlTranslateSelector
     */
    void buildQueryMap(String entity, String[] rule, List<String> scopes, List<String> props, String identity, QueryMap queryMap, SqlTranslateSelector sqlTranslateSelector);
}
