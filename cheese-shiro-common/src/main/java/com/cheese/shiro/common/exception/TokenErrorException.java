package com.cheese.shiro.common.exception;

/**
 * 令牌错误异常
 *
 * @author sobann
 */
public class TokenErrorException extends AuthException{
    /**
     * 令牌
     */
    private String token;

    public TokenErrorException() {
    }

    public TokenErrorException(String token) {
        this.token = token;
    }

    public TokenErrorException(String message, String token) {
        super(message);
        this.token = token;
    }

    public TokenErrorException(String message, Throwable cause, String token) {
        super(message, cause);
        this.token = token;
    }

    public TokenErrorException(Throwable cause, String token) {
        super(cause);
        this.token = token;
    }

    public TokenErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String token) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.token = token;
    }

}
