package com.cheese.shiro.common.exception;

/**
 * 用户会话信息丢失异常
 * @author sobann
 */
public class LostSessionException extends AuthException {
    /**
     * 用户身份信息
     */
    private String identity;

    public LostSessionException() {
    }

    public LostSessionException(String identity) {
        this.identity = identity;
    }

    public LostSessionException(String message, String identity) {
        super(message);
        this.identity = identity;
    }

    public LostSessionException(String message, Throwable cause, String identity) {
        super(message, cause);
        this.identity = identity;
    }

    public LostSessionException(Throwable cause, String identity) {
        super(cause);
        this.identity = identity;
    }

    public LostSessionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String identity) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }
}
