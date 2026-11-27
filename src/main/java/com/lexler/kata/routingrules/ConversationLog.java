package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.log.LogMessage;

import static com.lexler.kata.routingrules.constants.LogParams.*;
import static com.lexler.kata.routingrules.log.LogMessage.message;

final class ConversationLog {

    private ConversationLog() {
    }

    static LogMessage conversationMessage(BotEvent event, String text, String method) {
        return message(text, method)
            .param(CONVERSATION_ID, event.conversationId())
            .param(REQUEST_ID, event.requestId())
            .param(CHANNEL_ID, event.channelId());
    }
}
