package io.cify.framework.core

/**
 * Created by FOB Solutions
 */
class CifyFrameworkException extends Exception {

    /**
     * Cify framework exception with message
     *
     * @param message message to send
     * */
    public CifyFrameworkException(String message) {
        super(message)
    }

    /**
     * Cify framework exception with message and throwable
     *
     * @message message to send
     * @throwable another exception to pass
     * */
    public CifyFrameworkException(String message, Throwable throwable) {
        super(message, throwable)
    }
}
