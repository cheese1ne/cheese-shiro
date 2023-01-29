package com.cheese.shiro.server.core.manager.rule.parser;


import com.cheese.shiro.common.rule.RuleParser;

/**
 * 默认规则解释器管理器
 * @author sobann
 */
public interface RuleParserManager {
    /**
     * 获取匹配的解析器
     *
     * @param rule
     * @return
     */
    RuleParser matchParser(String rule);

    /**
     * 判断rule是否符合解析条件
     *
     * @param rule
     * @return
     */
    boolean isRuleMatch(String rule);
}
