package org.tbplusc.app.validator;

public class WordDistancePair {
    public final String hero;
    public final String alias;
    public int distance;

    public WordDistancePair(String hero, String alias, int distance) {
        this.distance = distance;
        this.hero = hero;
        this.alias = alias;
    }
}
