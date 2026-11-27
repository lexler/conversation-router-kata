package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.event.Coordinate;
import com.lexler.kata.routingrules.event.CustomerInfo;

final class RoutingStoryboard {

    private RoutingStoryboard() {
    }

    static String create(RoutingRules routingRules, BotEvent event) {
        QueueInfo routing = routingRules.getAssignedWorkQueue(event);
        return conversation(event.coordinate())
            + customer(event.customerInfo())
            + tags(event.customerInfo())
            + routing(routing);
    }

    private static String conversation(Coordinate page) {
        return line("Conversation", "%s page %s".formatted(page.networkKey(), page.externalId()));
    }

    private static String customer(CustomerInfo customer) {
        return line("Customer", "brand %s, %s, verification status %s".formatted(
            orNone(customer.customerBrand()), customer.bind() ? "bound" : "not bound", orNone(customer.status())));
    }

    private static String tags(CustomerInfo customer) {
        return line("Tags", "exec tag %s, exec mention %s, disruption mention %s".formatted(
            orNone(customer.authorExecTag()), orNone(customer.execMentionTag()), orNone(customer.disruptionMentionTag())));
    }

    private static String routing(QueueInfo routing) {
        return line("Router", "queue " + routing.queueId())
            + line("Router", routing.message());
    }

    private static String line(String speaker, String text) {
        return "[%12s]: %s\n".formatted(speaker, text);
    }

    private static String orNone(String value) {
        return value == null || value.isEmpty() ? "none" : value;
    }
}
