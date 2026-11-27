package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.log.Log;
import com.lexler.kata.routingrules.log.LogMessage;

import static com.lexler.kata.routingrules.constants.LogParams.*;
import static com.lexler.kata.routingrules.log.LogMessage.message;

final class RoutingLog {

    static final Log log = Log.forClass(RoutingRules.class);

    private static final String ROUTING_OPERATION = "getAssignedWorkQueue";

    private RoutingLog() {
    }

    static LogMessage routingMessage(String text) {
        return message(text, ROUTING_OPERATION);
    }

    static LogMessage conversationMessage(BotEvent event, String text) {
        return routingMessage(text)
            .param(CONVERSATION_ID, event.conversationId())
            .param(REQUEST_ID, event.requestId())
            .param(CHANNEL_ID, event.channelId());
    }
}
