package org.jahia.modules.npmplugins.jsengine;

/**
 * Base exception
 */
public class GraalVMException extends RuntimeException {
    public GraalVMException() {
    }

    public GraalVMException(String message) {
        super(message);
    }

    public GraalVMException(String message, Throwable cause) {
        super(message, cause);
    }

    public GraalVMException(Throwable cause) {
        super(cause);
    }

    public GraalVMException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
