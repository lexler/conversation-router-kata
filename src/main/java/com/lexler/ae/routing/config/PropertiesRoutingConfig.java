package com.lexler.ae.routing.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Properties;

public final class PropertiesRoutingConfig implements RoutingConfig {

    private final Properties properties;

    private PropertiesRoutingConfig(Properties properties) {
        this.properties = properties;
    }

    public static PropertiesRoutingConfig fromClasspath(String resource) {
        try (InputStream input = PropertiesRoutingConfig.class.getClassLoader().getResourceAsStream(resource)) {
            if (input == null) {
                throw new IllegalArgumentException("Config not found on classpath: " + resource);
            }
            Properties properties = new Properties();
            properties.load(input);
            return new PropertiesRoutingConfig(properties);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
}
