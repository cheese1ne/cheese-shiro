package com.cheese.shiro.common.table;

import java.io.Serializable;

/**
 * 实体时间 关系 表 映射
 *
 * @author sobann
 */
public class TableLink implements Serializable {
    private static final long serialVersionUID = -4837582663887735645L;
    private String entity;
    private String entityKey;
    private String scope;
    private String scopeKey;
    private String linkExpression;
    private String service;

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getLinkExpression() {
        return linkExpression;
    }

    public void setLinkExpression(String linkExpression) {
        this.linkExpression = linkExpression;
    }

    public String getEntityKey() {
        return entityKey;
    }

    public void setEntityKey(String entityKey) {
        this.entityKey = entityKey;
    }

    public String getScopeKey() {
        return scopeKey;
    }

    public void setScopeKey(String scopeKey) {
        this.scopeKey = scopeKey;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

}
