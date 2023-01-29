package com.cheese.shiro.common.exception;

/**
 * 用户身份管理器异常
 * @author sobann
 */
public class IdentityManagerException extends RuntimeException {
    private static final long serialVersionUID = -1315540427093113609L;

    public IdentityManagerException() {
        super("IdentityManager Execute Error,Please Ensure Manager Has Inited");
    }

    public IdentityManagerException(String message) {
        super(message);
    }

    public IdentityManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentityManagerException(Throwable cause) {
        super(cause);
    }

    public IdentityManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
