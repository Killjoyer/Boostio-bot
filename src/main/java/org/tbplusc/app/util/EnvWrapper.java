package org.tbplusc.app.util;

import java.util.HashMap;
import java.util.Map;

public class EnvWrapper {
    private static final Map<String, String> registeredValues = new HashMap<>();

    public static void registerValue(String key, String value) {
        registeredValues.put(key, value);
    }

    public static String getValue(String key) {
        return registeredValues.get(key);
    }
}
