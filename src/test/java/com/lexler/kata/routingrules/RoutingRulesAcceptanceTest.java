package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.config.RoutingConfig;
import com.lexler.kata.routingrules.config.PropertiesRoutingConfig;
import com.lexler.kata.routingrules.event.BotEvent;
import com.lexler.kata.routingrules.event.Coordinate;
import com.lexler.kata.routingrules.event.CustomerInfo;
import org.approvaltests.combinations.CombinationApprovals;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RoutingRulesAcceptanceTest {

    private final RoutingConfig config = PropertiesRoutingConfig.fromClasspath("routing.properties");
    private final RoutingRules routingRules = new RoutingRules(config);

    @Test
    void pinAll() {
        String[] channelIds = new String[]{
            "facebook_5001_200", "facebook_1001_200", "facebook_1002_200", "facebook_2001_200", "facebook_2002_200", "facebook_3001_200",
            "facebook_4001_200",
            "instagram_5001_200", "instagram_1003_200", "instagram_1004_200", "instagram_2003_200", "instagram_2004_200",
            "instagram_4002_200"};

        String[] brands = new String[]{"", "DuckWings", "FalconAir"};
        Boolean[] bindStatuses = new Boolean[]{true, false};
        String[] statuses = new String[]{"", "expired", "submitted", "bypass"};
        String[] authorTags = new String[]{"", "NO_EXEC_MENTION_TAG", "NO_AUTHOR_EXEC_TAG", "FalconAir", "DuckWings", "GR"};
        String[] execMentionTags = new String[]{"", "FalconAir", "DuckWings", "GR"};
        String[] disruptionTags = new String[]{"NO_DISRUPTION_TAG", "Disruption"};
        CombinationApprovals.verifyAllCombinations(this::assignWorkQueue,
            channelIds, brands, bindStatuses, statuses, authorTags, execMentionTags, disruptionTags);
    }

    @Test
    void exceptionCase() {
        RoutingRules routingRulesWithFailingConfig = new RoutingRules(key -> {
            if (key.equals("bot.queue")) {
                throw new RuntimeException("Expected exception");
            }
            return config.getProperty(key);
        });
        BotEvent event = new BotEvent("request-id-0", "500", new Coordinate("instagram", "5001"), "200", new CustomerInfo());

        QueueInfo result = routingRulesWithFailingConfig.getAssignedWorkQueue(event, event.conversationId(), event.requestId(), event.channelId());

        assertThat(result.message())
            .isEqualTo("An error occurred assigning workQueue. Sending to Facebook Falcon Care queue by default.201");
        assertThat(result.queueId())
            .isEqualTo("201");
    }

    private String assignWorkQueue(String channelId,
                                   String brand,
                                   boolean isBind,
                                   String status,
                                   String authorTag,
                                   String execMentionTag,
                                   String disruptionTag) {
        String channel = ChannelIdParser.getChannel(channelId);
        String pageId = ChannelIdParser.getPageId(channelId);
        BotEvent event = new BotEvent(
            "request-id-0",
            "500",
            new Coordinate(channel, pageId),
            "200",
            new CustomerInfo(brand, isBind, status, channel + "_" + pageId + "_200", authorTag, execMentionTag, disruptionTag));
        QueueInfo result = routingRules.getAssignedWorkQueue(event, event.conversationId(), event.requestId(), event.channelId());
        return formatResult(event, result);
    }

    private String formatResult(BotEvent event, QueueInfo info) {
        var board = "";
        board += "\nEvent:\n";
        board += String.format("  channelId: '%s'\n", event.customerInfo().channelId());
        board += String.format("  bound: '%s'\n", event.customerInfo().bind());
        board += String.format("  status: '%s'\n", event.customerInfo().status());
        return board + formatQueueInfo(info);
    }

    private String formatQueueInfo(QueueInfo info) {
        return String.format("Routed to:\n  queue: %s\n  message: %s\n", info.queueId(), info.message());
    }

    @Test
    @Deprecated
    void pinOnlyQueueIds() {
        String[] channelIds = new String[]{
            "facebook_5001_200", "facebook_1001_200", "facebook_1002_200", "facebook_2001_200", "facebook_2002_200", "facebook_3001_200",
            "facebook_4001_200",
            "instagram_5001_200", "instagram_1003_200", "instagram_1004_200", "instagram_2003_200", "instagram_2004_200",
            "instagram_4002_200"};

        String[] brands = new String[]{"", "DuckWings", "FalconAir"};
        Boolean[] bindStatuses = new Boolean[]{true, false};
        String[] statuses = new String[]{"", "expired", "submitted", "bypass"};
        String[] authorTags = new String[]{"", "NO_EXEC_MENTION_TAG", "NO_AUTHOR_EXEC_TAG", "FalconAir", "DuckWings", "GR"};
        String[] execMentionTags = new String[]{"", "FalconAir", "DuckWings", "GR"};
        String[] disruptionTags = new String[]{"NO_DISRUPTION_TAG", "Disruption"};
        CombinationApprovals.verifyAllCombinations(this::callRoutingRules,
            channelIds, brands, bindStatuses, statuses, authorTags, execMentionTags, disruptionTags);
    }

    private String callRoutingRules(String channelId,
                                    String brand,
                                    boolean isBind,
                                    String status,
                                    String authorTag,
                                    String execMentionTag,
                                    String disruptionTag) {
        String channel = ChannelIdParser.getChannel(channelId);
        String pageId = ChannelIdParser.getPageId(channelId);
        BotEvent event = new BotEvent(
            "request-id-0",
            "500",
            new Coordinate(channel, pageId),
            "200",
            new CustomerInfo(brand, isBind, status, channel + "_" + pageId + "_200", authorTag, execMentionTag, disruptionTag));
        QueueInfo result = routingRules.getAssignedWorkQueue(event, event.conversationId(), event.requestId(), event.channelId());
        return result.queueId();
    }
}