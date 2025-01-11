package ru.newvasuki.smarthome.data.type;

import java.util.HashMap;
import java.util.Map;

public final class DeviceTypeSingleton {

    DeviceTypeSingleton() {
        throw new IllegalStateException("Utility class");
    }

    public static final Map<String, DeviceType> lookupStr = new HashMap<>();
    public static final Map<Integer, DeviceType> lookupInt = new HashMap<>();
}
