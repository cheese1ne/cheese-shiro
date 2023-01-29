package com.cheese.shiro.common.service.entity;

/**
 * 规则权限
 * @author sobann
 */
public class RulePerm extends Identifier {
    private static final long serialVersionUID = -739187128099382578L;
    private String rule;

    public RulePerm() {
    }

    public RulePerm(String identifier, String rule) {
        super(identifier);
        this.rule = rule;
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public boolean isValid(){
        return rule !=null && rule.length()>0;
    }
}
