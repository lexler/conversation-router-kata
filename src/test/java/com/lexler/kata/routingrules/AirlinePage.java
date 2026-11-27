package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.event.Coordinate;

enum AirlinePage {
    FACEBOOK_HOME_MARKET("facebook", "5001", "Facebook, home market");

    private final Coordinate coordinate;
    private final String description;

    AirlinePage(String channel, String pageId, String description) {
        this.coordinate = new Coordinate(channel, pageId);
        this.description = description;
    }

    Coordinate coordinate() {
        return coordinate;
    }

    String description() {
        return description;
    }
}
