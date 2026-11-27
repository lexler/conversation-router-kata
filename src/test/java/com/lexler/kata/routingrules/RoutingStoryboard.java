package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.event.CustomerInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lexler.kata.routingrules.constants.ConstantStrings.DISRUPTION;
import static com.lexler.kata.routingrules.constants.DefaultStrings.*;

final class RoutingStoryboard {

    private static final Map<String, String> QUEUE_NAMES = Map.of(
        "100", "Bot queue");

    private RoutingStoryboard() {
    }

    static String create(RoutingRules routingRules, AirlinePage page, CustomerInfo customer) {
        BotEvent event = new BotEvent("request-1", "conversation-1", page.coordinate(), "200", customer);
        QueueInfo routing = routingRules.getAssignedWorkQueue(event);
        return customerFacts(customer)
            + fact("Writes on", page.description())
            + "\n"
            + outcome(routing);
    }

    private static String customerFacts(CustomerInfo customer) {
        String facts = fact("Customer", verification(customer));
        if (isSet(customer.customerBrand(), NO_CUSTOMER_BRAND)) {
            facts += fact("Brand", customer.customerBrand());
        }
        if (isSet(customer.authorExecTag(), NO_AUTHOR_EXEC_TAG)) {
            facts += fact("VIP of", customer.authorExecTag());
        }
        List<String> mentions = mentions(customer);
        if (!mentions.isEmpty()) {
            facts += fact("Mentions", String.join(", ", mentions));
        }
        return facts;
    }

    private static String verification(CustomerInfo customer) {
        if (customer.bind()) {
            return "verified";
        }
        if (isSet(customer.status(), NO_STATUS)) {
            return "not verified, verification " + customer.status();
        }
        return "not verified yet";
    }

    private static List<String> mentions(CustomerInfo customer) {
        List<String> mentions = new ArrayList<>();
        if (isSet(customer.execMentionTag(), NO_EXEC_MENTION_TAG)) {
            mentions.add("an executive of " + customer.execMentionTag());
        }
        if (DISRUPTION.equalsIgnoreCase(customer.disruptionMentionTag())) {
            mentions.add("a disruption");
        }
        return mentions;
    }

    private static String outcome(QueueInfo routing) {
        return "→ " + QUEUE_NAMES.getOrDefault(routing.queueId(), "queue " + routing.queueId()) + "\n"
            + "  \"" + withoutQueueId(routing) + "\"\n";
    }

    private static String withoutQueueId(QueueInfo routing) {
        String message = routing.message();
        return message.endsWith(routing.queueId())
            ? message.substring(0, message.length() - routing.queueId().length()).stripTrailing()
            : message;
    }

    private static String fact(String label, String value) {
        return "%-11s%s\n".formatted(label + ":", value);
    }

    private static boolean isSet(String value, String unsetMarker) {
        return value != null && !value.isEmpty() && !unsetMarker.equalsIgnoreCase(value);
    }
}
