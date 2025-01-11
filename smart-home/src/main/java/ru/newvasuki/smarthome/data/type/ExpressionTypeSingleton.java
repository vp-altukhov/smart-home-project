package ru.newvasuki.smarthome.data.type;

import java.util.HashMap;
import java.util.Map;

public final class ExpressionTypeSingleton {

    ExpressionTypeSingleton() {
        throw new IllegalStateException("Utility class");
    }

    public static final Map<String, ExpressionType> lookupStr = new HashMap<>();
    public static final Map<Integer, ExpressionType> lookupInt = new HashMap<>();
}
