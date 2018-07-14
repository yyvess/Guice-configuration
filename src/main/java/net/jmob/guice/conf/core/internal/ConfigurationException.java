package net.jmob.guice.conf.core.internal;

public class ConfigurationException extends RuntimeException {

    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(IllegalAccessException e) {
        super(e);
    }
}
