package com.lexler.ae.routing.config;

@FunctionalInterface
public interface RoutingConfig {
    String getProperty(String key);
}
