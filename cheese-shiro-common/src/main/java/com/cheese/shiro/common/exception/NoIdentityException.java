package com.cheese.shiro.common.exception;

/**
 * 无身份信息异常
 *
 * @author sobann
 */
public class NoIdentityException extends RuntimeException {
    private static final long serialVersionUID = -35522306854796653L;
    public NoIdentityException() {
        super("Can Not Find Identity For Current Thread");
    }

    public NoIdentityException(String message) {
        super(message);
    }

    public NoIdentityException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoIdentityException(Throwable cause) {
        super(cause);
    }

    public NoIdentityException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
