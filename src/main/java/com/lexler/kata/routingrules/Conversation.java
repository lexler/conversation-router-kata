package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.event.CustomerInfo;
import com.lexler.kata.routingrules.util.StringUtils;

import static com.lexler.kata.routingrules.constants.ConstantStrings.*;
import static com.lexler.kata.routingrules.constants.DefaultStrings.*;

record Conversation(BotEvent event, CustomerInfo customer, Page page) {

    static Conversation of(BotEvent event, Page page) {
        CustomerInfo customer = event.customerInfo() == null ? new CustomerInfo() : event.customerInfo();
        return new Conversation(event, customer, page);
    }

    boolean isBound() {
        return customer.bind();
    }

    String verificationStatus() {
        return orDefault(customer.status(), NO_STATUS);
    }

    String execTag() {
        return orDefault(customer.authorExecTag(), NO_AUTHOR_EXEC_TAG);
    }

    String execMention() {
        return orDefault(customer.execMentionTag(), NO_EXEC_MENTION_TAG);
    }

    String brand() {
        return orDefault(customer.customerBrand(), NO_CUSTOMER_BRAND);
    }

    boolean awaitsVerification() {
        return !isBound() && NO_STATUS.equalsIgnoreCase(verificationStatus());
    }

    boolean isReadyForSupportTeam() {
        String status = verificationStatus();
        return isBound() || status.contains(EXPIRED) || status.contains(SUBMITTED) || status.contains(BYPASS);
    }

    boolean isVerified() {
        return isBound() || verificationStatus().contains(SUBMITTED);
    }

    boolean hasExecTag() {
        return !NO_AUTHOR_EXEC_TAG.equalsIgnoreCase(execTag());
    }

    boolean hasExecMention() {
        return !NO_EXEC_MENTION_TAG.equalsIgnoreCase(execMention());
    }

    boolean hasUnknownBrand() {
        return NO_CUSTOMER_BRAND.equalsIgnoreCase(brand());
    }

    boolean isDuckWingsCustomer() {
        return DUCKWINGS.equalsIgnoreCase(brand());
    }

    boolean mentionsDisruption() {
        return DISRUPTION.equalsIgnoreCase(orDefault(customer.disruptionMentionTag(), NO_DISRUPTION_TAG));
    }

    private static String orDefault(String value, String fallback) {
        return StringUtils.isEmpty(value) ? fallback : value;
    }
}
