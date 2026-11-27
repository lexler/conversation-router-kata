package com.lexler.kata.routingrules;

import java.util.Map;

import static com.lexler.kata.routingrules.Page.Audience.*;
import static com.lexler.kata.routingrules.Page.Channel.FACEBOOK;
import static com.lexler.kata.routingrules.Page.Channel.INSTAGRAM;
import static com.lexler.kata.routingrules.RoutingLog.conversationMessage;
import static com.lexler.kata.routingrules.RoutingLog.log;
import static com.lexler.kata.routingrules.constants.LogParams.CUSTOMER_BRAND;
import static com.lexler.kata.routingrules.constants.LogParams.EVENT_STATUS;
import static com.lexler.kata.routingrules.constants.RouteMessages.*;

final class BrandRoutes {

    private static final Map<Page, Route> FALCONAIR_MARKET_ROUTES = Map.of(
        new Page(INSTAGRAM, GREECE), new Route("gr.instagram", FALCONAIR_CUSTOMER_GR_INSTAGRAM),
        new Page(FACEBOOK, GREECE), new Route("gr.facebook", FALCONAIR_CUSTOMER_GR_FB),
        new Page(FACEBOOK, CYPRUS), new Route("cy.facebook", FALCONAIR_CUSTOMER_CY_FB),
        new Page(FACEBOOK, BUSINESS), new Route("biz.facebook", FALCONAIR_CUSTOMER_BIZ_FB),
        new Page(INSTAGRAM, BUSINESS), new Route("biz.instagram", FALCONAIR_CUSTOMER_BIZ_INSTAGRAM));

    private static final Map<Page, Route> DUCKWINGS_MARKET_ROUTES = Map.of(
        new Page(INSTAGRAM, FRENCH_SPEAKERS), new Route("duck.french.instagram", DUCKWINGS_CUSTOMER_FRENCH_INSTAGRAM),
        new Page(FACEBOOK, FRENCH_SPEAKERS), new Route("duck.french.fb", DUCKWINGS_CUSTOMER_FRENCH_FB),
        new Page(INSTAGRAM, GREECE), new Route("duck.gr.instagram", DUCKWINGS_CUSTOMER_GR_INSTAGRAM),
        new Page(FACEBOOK, GREECE), new Route("duck.gr.fb", DUCKWINGS_CUSTOMER_GR_FB),
        new Page(FACEBOOK, CYPRUS), new Route("duck.cy.facebook", DUCKWINGS_CUSTOMER_CY_FB));

    Route routeFor(Conversation conversation) {
        if (conversation.hasUnknownBrand()) {
            return unknownBrandRoute(conversation);
        }
        log.info(conversationMessage(conversation.event(), "Checking customer brand."));
        return conversation.isDuckWingsCustomer() ? duckWingsRoute(conversation) : falconAirRoute(conversation);
    }

    private Route falconAirRoute(Conversation conversation) {
        log.info(conversationMessage(conversation.event(), "Customer identified as non DuckWings customer")
            .param(EVENT_STATUS, conversation.verificationStatus())
            .param(CUSTOMER_BRAND, conversation.brand()));
        Page page = conversation.page();
        if (page.serves(FRENCH_SPEAKERS)) {
            return new Route("falcon.french.care", FALCONAIR_CUSTOMER_FRENCH);
        }
        return FALCONAIR_MARKET_ROUTES.getOrDefault(page,
            falconCareRoute(conversation, FALCONAIR_CUSTOMER_INSTAGRAM, FALCONAIR_CUSTOMER_FB));
    }

    private Route duckWingsRoute(Conversation conversation) {
        log.info(conversationMessage(conversation.event(), "Customer identified as DuckWings customer"));
        Page page = conversation.page();
        return DUCKWINGS_MARKET_ROUTES.getOrDefault(page, duckWingsCareRoute(page));
    }

    private static Route duckWingsCareRoute(Page page) {
        return page.isOnInstagram()
            ? new Route("duck.care.instagram", DUCKWINGS_CUSTOMER_INSTAGRAM)
            : new Route("duck.care.fb", DUCKWINGS_CUSTOMER_FB);
    }

    private Route unknownBrandRoute(Conversation conversation) {
        log.info(conversationMessage(conversation.event(), "Conversation has no customer brand. Assigning default falconair queue based on network type and pageid.")
            .param(CUSTOMER_BRAND, conversation.brand()));
        Page page = conversation.page();
        return switch (page.audience()) {
            case FRENCH_SPEAKERS -> new Route("falcon.french.care", NO_CUSTOMER_BRAND_FRENCH_PAGE);
            case GREECE -> page.isOnInstagram()
                ? new Route("gr.instagram", NO_CUSTOMER_BRAND_GR_INSTAGRAM)
                : new Route("gr.facebook", NO_CUSTOMER_BRAND_GR_FB);
            case CYPRUS -> new Route("cy.facebook", NO_CUSTOMER_BRAND_CY_FB);
            case BUSINESS -> page.isOnInstagram()
                ? new Route("biz.instagram", NO_CUSTOMER_BRAND_BIZ_INSTAGRAM)
                : new Route("biz.facebook", NO_CUSTOMER_BRAND_BIZ_FB);
            case HOME_MARKET -> falconCareRoute(conversation, NO_CUSTOMER_BRAND_INSTAGRAM, NO_CUSTOMER_BRAND_FB);
        };
    }

    private static Route falconCareRoute(Conversation conversation, String instagramReason, String facebookReason) {
        if (conversation.mentionsDisruption()) {
            return disruptionRoute(conversation.page());
        }
        return conversation.page().isOnInstagram()
            ? new Route("falcon.instagram.care", instagramReason)
            : new Route("falcon.fb.care", facebookReason);
    }

    private static Route disruptionRoute(Page page) {
        return page.isOnInstagram()
            ? new Route("falcon.instagram.disruption", DISRUPTION_MENTION + INSTAGRAM_CHANNEL)
            : new Route("falcon.fb.disruption", DISRUPTION_MENTION + FACEBOOK_CHANNEL);
    }
}
