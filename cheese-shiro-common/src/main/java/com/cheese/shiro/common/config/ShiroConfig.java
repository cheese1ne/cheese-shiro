package com.cheese.shiro.common.config;

import java.io.Serializable;

/**
 * 权限信息配置，可通过继承使用配置文件进行参数设置
 *
 * @author sobann
 */
public class ShiroConfig implements Serializable {
    private static final long serialVersionUID = -1681156003765490330L;

    /**
     * TOKEN解析时存储的关键KEY
     * 对于JWT即为加密和解密的KEY
     */
    private String tokenKey;

    /**
     * TOKEN的ID值
     */
    private String tokenId;

    /**
     * TOKEN有效期
     */
    private long expire;

    /**
     * 请求与相应中头部中TOKEN的关键字
     */
    private String tokenName;

    /**
     * TOKEN在服务间传递时候使用的名称(线程中保存使用，各服务之间身份信息传递)
     */
    private String contextTracerName;

    /**
     * 默认权限信息(游客权限)
     */
    private String defaultIdentity;


    public String getTokenKey() {
        return tokenKey;
    }

    public void setTokenKey(String tokenKey) {
        this.tokenKey = tokenKey;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public long getExpire() {
        return expire;
    }

    public void setExpire(long expire) {
        this.expire = expire;
    }

    public String getTokenName() {
        return tokenName;
    }

    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    public String getContextTracerName() {
        return contextTracerName;
    }

    public void setContextTracerName(String contextTracerName) {
        this.contextTracerName = contextTracerName;
    }

    public String getDefaultIdentity() {
        return defaultIdentity;
    }

    public void setDefaultIdentity(String defaultIdentity) {
        this.defaultIdentity = defaultIdentity;
    }

}
