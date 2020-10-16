package org.tbplusc.app.validator;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Comparator;
import java.util.List;


public class Validator {
    private final List<String> charactersNames;
    private final LevenshteinDistance levenshteinComparer = new LevenshteinDistance();

    private Comparator<String> getComporator(String s) {
        return Comparator.comparing(t -> applyComparing(t.toLowerCase(), s));
    }

    private int applyComparing(String s1, String s2) {
        return levenshteinComparer.apply(s1, s2);
    }

    public Validator(List<String> heroes) throws IllegalArgumentException {
        if (heroes == null || heroes.isEmpty()) {
            throw new IllegalArgumentException();
        }
        charactersNames = heroes;
    }

    public String getClosestToInput(String userInput) {
        var loweredInput = userInput.toLowerCase();
        return charactersNames.stream()
                .min(getComporator(loweredInput))
                .get();
    }

    public WordDistancePair[] getSomeCosestToInput(String userInput, int length) {
        var loweredInput = userInput.toLowerCase();
        return charactersNames.stream()
                .map(s -> new WordDistancePair(s, applyComparing(s, loweredInput)))
                .sorted(Comparator.comparingInt(s -> s.distance))
                .limit(length)
                .toArray(WordDistancePair[]::new);
    }
}
