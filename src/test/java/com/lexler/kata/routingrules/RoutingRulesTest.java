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

        Approvals.verifyAll(tableOfContents(scenarios), scenarios, this::scenarioSection, new Options().forFile().withExtension(".md"));
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

    private String scenarioSection(Scenario scenario) {
        return """
            ### Scenario %s
            #### %s
            ```
            %s```
            ---
            """.formatted(scenario.number(), scenario.title(), RoutingStoryboard.create(routingRules, scenario.event()));
    }
}
