package com.cheese.shiro.client.mybatis.manager;

import com.cheese.shiro.common.anno.AuthKey;
import org.apache.commons.lang.StringUtils;

/**
 * 权限信息收集实体
 *
 * @author sobann
 */
public class AuthKeyInfo {

    private String key;
    private String scope;
    private String entity;
    private String action;
    private boolean ignoreLevel;
    private String app;

    public AuthKeyInfo() {

    }

    public AuthKeyInfo(AuthKey authKey) {
        this.key = authKey.key();
        this.entity = authKey.entity();
        this.scope = authKey.scope();
        this.action = authKey.action();
        this.ignoreLevel = authKey.ignoreLevel();
        this.app = authKey.app();
    }

    public void reset(AuthKey authKey) {
        if (StringUtils.isNotBlank(authKey.entity())) {
            this.entity = authKey.entity();
        }

        if (StringUtils.isNotBlank(authKey.key())) {
            this.key = authKey.key();
        }
        if (StringUtils.isNotBlank(authKey.scope())) {
            this.scope = authKey.scope();
        }
        if (StringUtils.isNotBlank(authKey.action())) {
            this.action = authKey.action();
        }
        this.ignoreLevel = authKey.ignoreLevel();
        if (StringUtils.isNotBlank(authKey.app())) {
            this.app = authKey.app();
        }
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public boolean isIgnoreLevel() {
        return ignoreLevel;
    }

    public void setIgnoreLevel(boolean ignoreLevel) {
        this.ignoreLevel = ignoreLevel;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
