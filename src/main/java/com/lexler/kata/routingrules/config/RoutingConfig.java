package com.lexler.kata.routingrules.config;

@FunctionalInterface
public interface RoutingConfig {
    String getProperty(String key);
}
