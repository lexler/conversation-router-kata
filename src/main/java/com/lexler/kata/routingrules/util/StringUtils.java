package com.lexler.kata.routingrules.util;

public final class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }
}
