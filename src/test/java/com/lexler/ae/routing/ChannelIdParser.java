package com.lexler.ae.routing;

final class ChannelIdParser {

    private ChannelIdParser() {
    }

    static String getChannel(String channelId) {
        return parse(channelId, 0);
    }

    static String getPageId(String channelId) {
        return parse(channelId, 1);
    }

    private static String parse(String channelId, int index) {
        String[] parts = channelId.split("_");
        return parts.length == 3 ? parts[index] : "";
    }
}
