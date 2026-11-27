package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.config.PropertiesRoutingConfig;
import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.event.Coordinate;
import com.lexler.kata.routingrules.event.CustomerInfo;
import org.approvaltests.Approvals;
import org.approvaltests.core.Options;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

class RoutingRulesTest {

    private final RoutingRules routingRules = new RoutingRules(PropertiesRoutingConfig.fromClasspath("routing.properties"));

    @Test
    void routesConversations() {
        List<Scenario> scenarios = List.of(
            new Scenario(1, "Unverified customer writes on a home market Facebook page",
                conversationOn("facebook", "5001", new CustomerInfo())));

        Approvals.verifyAll(tableOfContents(scenarios), scenarios, this::storyboard, new Options().forFile().withExtension(".md"));
    }

    private record Scenario(int number, String title, BotEvent event) {
    }

    private BotEvent conversationOn(String channel, String pageId, CustomerInfo customer) {
        return new BotEvent("request-1", "conversation-1", new Coordinate(channel, pageId), "200", customer);
    }

    private String tableOfContents(List<Scenario> scenarios) {
        return "# Routing rules\n\n" + scenarios.stream()
            .map(scenario -> "1. [%s](#scenario-%s)".formatted(scenario.title(), scenario.number()))
            .collect(Collectors.joining("\n"));
    }

    private String storyboard(Scenario scenario) {
        return """
            ### Scenario %s
            #### %s
            ```
            %s```
            ---
            """.formatted(scenario.number(), scenario.title(), routingOf(scenario.event()));
    }

    private String routingOf(BotEvent event) {
        CustomerInfo customer = event.customerInfo();
        QueueInfo routing = routingRules.getAssignedWorkQueue(event);
        return line("Conversation", "%s page %s".formatted(event.coordinate().networkKey(), event.coordinate().externalId()))
            + line("Customer", "brand %s, %s, verification status %s".formatted(
                orNone(customer.customerBrand()), customer.bind() ? "bound" : "not bound", orNone(customer.status())))
            + line("Tags", "exec tag %s, exec mention %s, disruption mention %s".formatted(
                orNone(customer.authorExecTag()), orNone(customer.execMentionTag()), orNone(customer.disruptionMentionTag())))
            + line("Router", "queue " + routing.queueId())
            + line("Router", routing.message());
    }

    private String line(String speaker, String text) {
        return "[%12s]: %s\n".formatted(speaker, text);
    }

    private String orNone(String value) {
        return value == null || value.isEmpty() ? "none" : value;
    }
}
