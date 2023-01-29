package com.cheese.shiro.common.exception;

/**
 * 无可用数据
 * 对应数据权限为空
 *
 * @author sobann
 */
public class NoAccessDataException extends RuntimeException {
    /**
     * 权限实体
     */
    private String entity;
    /**
     * 用户身份信息
     */
    private String identity;

    public NoAccessDataException() {
    }

    public NoAccessDataException(String entity, String identity) {
        this.entity = entity;
        this.identity = identity;
    }

    public NoAccessDataException(String message, String entity, String identity) {
        super(message);
        this.entity = entity;
        this.identity = identity;
    }

    public NoAccessDataException(String message, Throwable cause, String entity, String identity) {
        super(message, cause);
        this.entity = entity;
        this.identity = identity;
    }

    public NoAccessDataException(Throwable cause, String entity, String identity) {
        super(cause);
        this.entity = entity;
        this.identity = identity;
    }

    public NoAccessDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, String entity, String identity) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.entity = entity;
        this.identity = identity;
    }

}
