package com.fit2cloud.commons.server.exception;

public class F2CException extends RuntimeException {

    private F2CException(String message) {
        super(message);
    }

    private F2CException(Throwable t) {
        super(t);
    }

    public static void throwException(String message) {
        throw new F2CException(message);
    }

    public static F2CException getException(String message) {
        throw new F2CException(message);
    }

    public static void throwException(Throwable t) {
        throw new F2CException(t);
    }
}
