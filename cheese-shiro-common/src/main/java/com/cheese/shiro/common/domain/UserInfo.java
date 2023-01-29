package com.cheese.shiro.common.domain;

import java.io.Serializable;

/**
 * 用户身份信息，可扩展保存各式数据
 * @author sobann
 */
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1251067343237529435L;
    /**
     * RSA加密盐
     */
    private String salt;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 认证标识，即用户id
     */
    private String identity;

    public UserInfo() {
    }

    public UserInfo(String password, String identity) {
        this.password = password;
        this.identity = identity;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
