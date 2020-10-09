package org.tbplusc.app.util;

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import org.tbplusc.app.talenthelper.Hero;

import com.google.gson.Gson;

public class JsonDeserializer {
    private JsonDeserializer() {
        throw new IllegalStateException("Utility class");
    }

    private static Type heroArrayListType = new TypeToken<List<Hero>>() {
    }.getType();

    public static <T> T deserializeJson(String json, Type type) {
        return new Gson().fromJson(json, type);
    }

    public static List<Hero> deserializeHeroList(String json) {
        return deserializeJson(json, heroArrayListType);
    }
}
