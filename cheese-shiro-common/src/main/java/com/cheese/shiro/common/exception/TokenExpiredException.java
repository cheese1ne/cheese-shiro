package com.cheese.shiro.common.exception;

/**
 * 令牌过期异常
 *
 * @author sobann
 */
public class TokenExpiredException extends AuthException {
    /**
     * 令牌
     */
    private String token;

    public TokenExpiredException() {
    }

    public TokenExpiredException(String token) {
        this.token = token;
    }

    public TokenExpiredException(String message, String token) {
        super(message);
        this.token = token;
    }

    public TokenExpiredException(String message, Throwable cause, String token) {
        super(message, cause);
        this.token = token;
    }

    public TokenExpiredException(Throwable cause, String token) {
        super(cause);
        this.token = token;
    }

    public TokenExpiredException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String token) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.token = token;
    }

}
