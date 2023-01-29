package com.cheese.shiro.server.core.manager.rule.parser;

import com.cheese.shiro.common.rule.RuleParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 默认规则解释器管理器
 * @author sobann
 */
public class DefaultRuleParseManager implements RuleParserManager {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRuleParseManager.class);

    private List<RuleParser> ruleParsers = new ArrayList<>();

    private Comparator<RuleParser> comparator = new Comparator<RuleParser>() {
        @Override
        public int compare(RuleParser o1, RuleParser o2) {
            return o1.order()-o2.order();
        }
    };

    public List<RuleParser> getRuleParsers() {
        return ruleParsers;
    }

    public void setRuleParsers(List<RuleParser> ruleParsers) {
        this.ruleParsers = ruleParsers;
        Collections.sort(ruleParsers,comparator);
    }

    public void addRuleParser(RuleParser ruleParser){
        if(ruleParsers == null){
            ruleParsers = new ArrayList<>();
        }
        ruleParsers.add(ruleParser);
        Collections.sort(ruleParsers,comparator);
    }

    @Override
    public RuleParser matchParser(String rule) {
        for (RuleParser ruleParser : ruleParsers) {
            if(ruleParser.match(rule)){
                return ruleParser;
            }
        }
        logger.error("Can Not Find matchParser For {}",rule);
        return null;
    }

    @Override
    public boolean isRuleMatch(String rule) {
        for (RuleParser ruleParser : ruleParsers) {
            if(ruleParser.match(rule)){
                return true;
            }
        }
        return false;
    }


}
