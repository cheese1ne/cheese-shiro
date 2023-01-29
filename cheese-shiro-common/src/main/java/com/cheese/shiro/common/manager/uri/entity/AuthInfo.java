package com.cheese.shiro.common.manager.uri.entity;


import com.cheese.shiro.common.anno.Auth;

import java.io.Serializable;

/**
 * Auth接口层权限信息抽取
 * @author sobann
 */
public class AuthInfo implements Serializable {
    private static final long serialVersionUID = 107190841671551316L;
    private String identifier;
    private String instanceId = "_";
    private boolean login;
    private String regex = "";
    private int index = 1;
    private String app ="";


    public AuthInfo() {

    }

    public AuthInfo(Auth auth) {
        this.identifier = auth.identifier();
        this.instanceId = auth.instanceId();
        this.login = auth.login();
        this.regex = auth.regex();
        this.index = auth.index();
        this.app = auth.app();
    }


    public AuthInfo(String identifier) {
        this.identifier = identifier;
    }

    public AuthInfo(String identifier, String instanceId) {
        this.identifier = identifier;
        this.instanceId = instanceId;
    }

    public AuthInfo(String identifier, String instanceId, boolean login) {
        this.identifier = identifier;
        this.instanceId = instanceId;
        this.login = login;
    }

    public AuthInfo(String identifier, String instanceId, boolean login, String regex, int index,String app) {
        this.identifier = identifier;
        this.instanceId = instanceId;
        this.login = login;
        this.regex = regex;
        this.index = index;
        this.app = app;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public boolean isLogin() {
        return login;
    }

    public void setLogin(boolean login) {
        this.login = login;
    }

    public String getRegex() {
        return regex;
    }

    public void setRegex(String regex) {
        this.regex = regex;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }
}
