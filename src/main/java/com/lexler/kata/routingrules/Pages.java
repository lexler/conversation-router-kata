package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.Page.Audience;
import com.lexler.kata.routingrules.Page.Channel;
import com.lexler.kata.routingrules.config.RoutingConfig;
import com.lexler.kata.routingrules.event.Coordinate;

import java.util.Arrays;

import static com.lexler.kata.routingrules.Page.Audience.*;

final class Pages {

    private final RoutingConfig config;

    Pages(RoutingConfig config) {
        this.config = config;
    }

    Page pageAt(Coordinate coordinate) {
        Audience audience = audienceOf(coordinate.externalId());
        return new Page(Channel.named(coordinate.networkKey()), audience);
    }

    private Audience audienceOf(String pageId) {
        if (isAnyOf(pageId, "falcon.instagram.fr.pageid", "falcon.fb.french.pageid", "duck.instagram.fr.pageid", "duck.fb.french.pagid")) {
            return FRENCH_SPEAKERS;
        }
        if (isAnyOf(pageId, "falcon.instagram.gr.pageid", "falcon.fb.gr.pageid", "duck.instagram.gr.pageid", "duck.fb.gr.pagid")) {
            return GREECE;
        }
        if (isAnyOf(pageId, "falcon.facebook.cy.pageid")) {
            return CYPRUS;
        }
        if (isAnyOf(pageId, "falcon.instagram.biz.pageid", "falcon.fb.biz.pageid")) {
            return BUSINESS;
        }
        return HOME_MARKET;
    }

    private boolean isAnyOf(String pageId, String... pageIdKeys) {
        return Arrays.stream(pageIdKeys)
            .map(config::getProperty)
            .anyMatch(pageIdKey -> pageId.equalsIgnoreCase(pageIdKey));
    }
}
