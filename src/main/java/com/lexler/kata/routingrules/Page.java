package com.lexler.kata.routingrules;

import com.lexler.kata.routingrules.constants.ConstantStrings;

record Page(Channel channel, Audience audience) {

    enum Channel {
        FACEBOOK, INSTAGRAM, UNKNOWN;

        static Channel named(String networkKey) {
            if (ConstantStrings.FACEBOOK.equalsIgnoreCase(networkKey)) {
                return FACEBOOK;
            }
            if (ConstantStrings.INSTAGRAM.equalsIgnoreCase(networkKey)) {
                return INSTAGRAM;
            }
            return UNKNOWN;
        }
    }

    enum Audience {
        HOME_MARKET, FRENCH_SPEAKERS, GREECE, CYPRUS, BUSINESS
    }

    boolean isOnInstagram() {
        return channel == Channel.INSTAGRAM;
    }

    boolean serves(Audience audience) {
        return this.audience == audience;
    }
}
