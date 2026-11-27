package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.config.RoutingConfig;

import static com.lexler.kata.routingrules.constants.RouteMessages.ERROR_MESSAGE;

record Route(String queueKey, String reason) {

    static final Route NOWHERE = new Route(null, "");
    static final Route FALLBACK = new Route("falcon.fb.care", ERROR_MESSAGE);

    String queueIdIn(RoutingConfig config) {
        return queueKey == null ? "" : config.getProperty(queueKey);
    }
}
