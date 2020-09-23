package com.fit2cloud.plugin.container.sdk;

public class ContainerPluginException extends RuntimeException {

    public ContainerPluginException() {
        super();
    }

    public ContainerPluginException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ContainerPluginException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContainerPluginException(String message) {
        super(message);
    }

    public ContainerPluginException(Throwable cause) {
        super(cause);
    }
}
