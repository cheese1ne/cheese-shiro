package com.cheese.shiro.common.exception;

/**
 * 用户多登陆异常
 * @author sobann
 */
public class MultiLoginException extends AuthException {
    /**
     * 用户认证信息
     */
    private String identity;
    /**
     * 原始ip
     */
    private String originalIp;
    /**
     * 当前ip
     */
    private String currentIp;

    public MultiLoginException() {
    }

    public MultiLoginException(String identity, String originalIp, String currentIp) {
        this.identity = identity;
        this.originalIp = originalIp;
        this.currentIp = currentIp;
    }

    public MultiLoginException(String message, String identity, String originalIp, String currentIp) {
        super(message);
        this.identity = identity;
        this.originalIp = originalIp;
        this.currentIp = currentIp;
    }

    public MultiLoginException(String message, Throwable cause, String identity, String originalIp, String currentIp) {
        super(message, cause);
        this.identity = identity;
        this.originalIp = originalIp;
        this.currentIp = currentIp;
    }

    public MultiLoginException(Throwable cause, String identity, String originalIp, String currentIp) {
        super(cause);
        this.identity = identity;
        this.originalIp = originalIp;
        this.currentIp = currentIp;
    }

    public MultiLoginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String identity, String originalIp, String currentIp) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.identity = identity;
        this.originalIp = originalIp;
        this.currentIp = currentIp;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getOriginalIp() {
        return originalIp;
    }

    public void setOriginalIp(String originalIp) {
        this.originalIp = originalIp;
    }

    public String getCurrentIp() {
        return currentIp;
    }

    public void setCurrentIp(String currentIp) {
        this.currentIp = currentIp;
    }
}

