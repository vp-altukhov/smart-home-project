package ru.newvasuki.smarthome.data.type;

import java.util.HashMap;
import java.util.Map;

public final class OperationTypeSingleton {

    OperationTypeSingleton() {
        throw new IllegalStateException("Utility class");
    }

    public static final Map<String, OperationType> lookupStr = new HashMap<>();
    public static final Map<Integer, OperationType> lookupInt = new HashMap<>();
}
