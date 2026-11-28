package com.lexler.kata.routingrules.event;

public record CustomerInfo(
    String customerBrand,
    boolean bind,
    String status,
    String channelId,
    String authorExecTag,
    String execMentionTag,
    String disruptionMentionTag) {

    public CustomerInfo() {
        this(null, false, null, null, null, null, null);
    }
}
