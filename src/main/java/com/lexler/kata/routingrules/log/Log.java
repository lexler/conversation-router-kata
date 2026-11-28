package com.lexler.kata.routingrules.log;

import java.lang.System.Logger.Level;

public final class Log {

    private final System.Logger logger;

    private Log(System.Logger logger) {
        this.logger = logger;
    }

    public static Log forClass(Class<?> owner) {
        return new Log(System.getLogger(owner.getName()));
    }

    public void debug(LogMessage message) {
        logger.log(Level.DEBUG, message::toString);
    }

    public void info(LogMessage message) {
        logger.log(Level.INFO, message::toString);
    }

    public void error(LogMessage message) {
        logger.log(Level.ERROR, message::toString);
    }
}
