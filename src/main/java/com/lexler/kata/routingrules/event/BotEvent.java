package com.lexler.kata.routingrules.event;

public record BotEvent(String requestId, String conversationId, Coordinate coordinate, String authorId, CustomerInfo customerInfo) {

    public String channelId() {
        return coordinate.networkKey() + "_" + coordinate.externalId() + "_" + authorId;
    }
}
