package com.cheese.shiro.common.manager.session;

import java.io.Serializable;
import java.util.Date;

/**
 * 会话信息存储
 *
 * @author sobann
 */
public class Session implements Serializable {
    private static final long serialVersionUID = 4005443816188951960L;
    /**
     * 用户身份信息
     */
    private String identity;
    /**
     * 用户登陆ip
     */
    private String ip;
    /**
     * 用户初始访问时间
     */
    private Date startTime;
    /**
     * Session过期时间，系统统一控制，与token过期时间一致
     */
    private Date expireTime;
    /**
     * session uuid 单用户控制使用
     */
    private String id;

    public Session() {
    }

    public Session(String identity, String ip, Date startTime, Date expireTime) {
        this.identity = identity;
        this.ip = ip;
        this.startTime = startTime;
        this.expireTime = expireTime;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
