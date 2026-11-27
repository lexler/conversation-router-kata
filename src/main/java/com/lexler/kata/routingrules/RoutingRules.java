package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.config.RoutingConfig;
import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.log.Log;
import com.lexler.kata.routingrules.log.LogMessage;

import java.util.Map;
import java.util.TreeMap;

import static com.lexler.kata.routingrules.Page.Audience.*;
import static com.lexler.kata.routingrules.Page.Channel.FACEBOOK;
import static com.lexler.kata.routingrules.Page.Channel.INSTAGRAM;
import static com.lexler.kata.routingrules.constants.ConstantStrings.DUCKWINGS;
import static com.lexler.kata.routingrules.constants.ConstantStrings.FALCONAIR;
import static com.lexler.kata.routingrules.constants.ConstantStrings.GR;
import static com.lexler.kata.routingrules.constants.LogParams.*;
import static com.lexler.kata.routingrules.constants.RouteMessages.*;
import static com.lexler.kata.routingrules.log.LogMessage.message;

public class RoutingRules {

    private static final Log log = Log.forClass(RoutingRules.class);

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

    private static final Map<String, Route> EXEC_TAG_ROUTES = ignoringCase(Map.of(
        FALCONAIR, new Route("falcon.exec.queue", AUTHOR_EXEC_FALCONAIR),
        DUCKWINGS, new Route("duckwings.exec.queue", AUTHOR_EXEC_DUCKWINGS),
        GR, new Route("gr.exec.queue", AUTHOR_EXEC_GR)));

    private static final Map<String, Route> EXEC_MENTION_ROUTES = ignoringCase(Map.of(
        FALCONAIR, new Route("falcon.exec.queue", EXEC_MENTION_FALCONAIR),
        DUCKWINGS, new Route("duckwings.exec.queue", EXEC_MENTION_DUCKWINGS),
        GR, new Route("gr.exec.queue", EXEC_MENTION_GR)));

    private final RoutingConfig config;
    private final Pages pages;

    public RoutingRules(RoutingConfig config) {
        this.config = config;
        this.pages = new Pages(config);
    }

    public QueueInfo getAssignedWorkQueue(BotEvent event) {
        log.info(conversationMessage(event, "Determining workQueue the conversation belongs to", "getAssignedWorkQueue"));
        QueueInfo assignedQueueInfo = assignQueue(event);
        log.info(message("Work Queue assigned to this conversation: " + assignedQueueInfo.queueId(), "getAssignedWorkQueue")
            .param(CONVERSATION_ID, event.conversationId()).param(WORKQUEUE_ID, assignedQueueInfo.queueId()).param(MESSAGE, assignedQueueInfo.message()).param(CHANNEL_ID, event.channelId()).param(REQUEST_ID, event.requestId()));
        return assignedQueueInfo;
    }

    private QueueInfo assignQueue(BotEvent event) {
        String verificationNote = "";
        try {
            Conversation conversation = conversationOf(event);
            verificationNote = conversation.isVerified() ? VERIFIED : "";
            return queueInfo(verificationNote, routeFor(conversation));
        } catch (Exception e) {
            log.error(message("An error occurred when trying to assign work queue id. Assigning default values", "getAssignedWorkQueue")
                .param(EVENT, event)
                .param(EXCEPTION, e.getMessage()));
            return queueInfo(verificationNote, Route.FALLBACK);
        }
    }

    private QueueInfo queueInfo(String verificationNote, Route route) {
        String queueId = route.queueIdIn(config);
        return new QueueInfo(verificationNote + route.reason() + queueId, queueId);
    }

    private Conversation conversationOf(BotEvent event) {
        Page page = pages.pageAt(event.coordinate());
        log.debug(conversationMessage(event, "Page: " + page, "getAssignedWorkQueue")
            .param(PAGE_ID, event.coordinate().externalId()));
        return Conversation.of(event, page);
    }

    private Route routeFor(Conversation conversation) {
        if (conversation.awaitsVerification()) {
            log.info(conversationMessage(conversation.event(), "Conversation is not bound, and status is empty or null. Assigning to the bot queue", "getAssignedWorkQueue")
                .param(BIND_STATUS, conversation.isBound())
                .param(EVENT_STATUS, conversation.verificationStatus()));
            return new Route("bot.queue", NOT_BOUND_BOT_QUEUE);
        }
        if (!conversation.isReadyForSupportTeam()) {
            return Route.NOWHERE;
        }
        log.info(conversationMessage(conversation.event(), "Conversation is either bound, bypassed or the timer expired. Assigning it to a new workQueue.", "getAssignedWorkQueue")
            .param(BIND_STATUS, conversation.isBound())
            .param(EVENT_STATUS, conversation.verificationStatus()));
        return supportTeamRoute(conversation);
    }

    private Route supportTeamRoute(Conversation conversation) {
        if (conversation.hasExecTag()) {
            return execTagRoute(conversation);
        }
        if (conversation.hasExecMention()) {
            return execMentionRoute(conversation);
        }
        return brandRoute(conversation);
    }

    private Route execTagRoute(Conversation conversation) {
        log.info(conversationMessage(conversation.event(), "Conversation has Author Exec tags. Assigning to one of the exec queues", "getAssignedWorkQueue")
            .param(BIND_STATUS, conversation.isBound())
            .param(EVENT_STATUS, conversation.verificationStatus()));
        return EXEC_TAG_ROUTES.getOrDefault(conversation.execTag(), Route.NOWHERE);
    }

    private Route execMentionRoute(Conversation conversation) {
        log.info(conversationMessage(conversation.event(), "Conversation has Exec mention tags.", "getAssignedWorkQueue")
            .param(EXEC_MENTION, conversation.execMention()));
        return EXEC_MENTION_ROUTES.getOrDefault(conversation.execMention(), Route.NOWHERE);
    }

    private Route brandRoute(Conversation conversation) {
        if (conversation.hasUnknownBrand()) {
            return unknownBrandRoute(conversation);
        }
        log.info(conversationMessage(conversation.event(), "Checking customer brand.", "getAssignedWorkQueue"));
        return conversation.isDuckWingsCustomer() ? duckWingsRoute(conversation) : falconAirRoute(conversation);
    }

    private Route falconAirRoute(Conversation conversation) {
        log.info(conversationMessage(conversation.event(), "Customer identified as non DuckWings customer", "getAssignedWorkQueue")
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
        log.info(conversationMessage(conversation.event(), "Customer identified as DuckWings customer", "getAssignedWorkQueue"));
        Page page = conversation.page();
        return DUCKWINGS_MARKET_ROUTES.getOrDefault(page, duckWingsCareRoute(page));
    }

    private static Route duckWingsCareRoute(Page page) {
        return page.isOnInstagram()
            ? new Route("duck.care.instagram", DUCKWINGS_CUSTOMER_INSTAGRAM)
            : new Route("duck.care.fb", DUCKWINGS_CUSTOMER_FB);
    }

    private Route unknownBrandRoute(Conversation conversation) {
        log.info(conversationMessage(conversation.event(), "Conversation has no customer brand. Assigning default falconair queue based on network type and pageid.", "getAssignedWorkQueue")
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

    private static Map<String, Route> ignoringCase(Map<String, Route> routesByTag) {
        Map<String, Route> routes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        routes.putAll(routesByTag);
        return routes;
    }

    private static LogMessage conversationMessage(BotEvent event, String text, String method) {
        return message(text, method)
            .param(CONVERSATION_ID, event.conversationId())
            .param(REQUEST_ID, event.requestId())
            .param(CHANNEL_ID, event.channelId());
    }
}
