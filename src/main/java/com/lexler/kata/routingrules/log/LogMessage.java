package com.lexler.kata.routingrules.log;

public final class LogMessage {

    private final StringBuilder text;

    private LogMessage(String text, String method) {
        this.text = new StringBuilder(method).append(": ").append(text);
    }

    public static LogMessage message(String text, String method) {
        return new LogMessage(text, method);
    }

    public LogMessage param(String name, Object value) {
        text.append(" || ").append(name).append('=').append(value);
        return this;
    }

    public LogMessage add(String context) {
        text.append(context);
        return this;
    }

    @Override
    public String toString() {
        return text.toString();
    }
}
