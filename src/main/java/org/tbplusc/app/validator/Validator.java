package org.tbplusc.app.validator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.apache.commons.text.similarity.LevenshteinDistance;


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
        return charactersNames.stream().min(getComporator(loweredInput)).get();
    }

    public WordDistancePair[] getSomeClosestToInput(String userInput, int length) {
        var loweredInput = userInput.toLowerCase();
        return charactersNames.stream()
                        .map(s -> new WordDistancePair(s, s,
                                        applyComparing(s.toLowerCase(), loweredInput)))
                        .sorted(Comparator.comparingInt(s -> s.distance)).limit(length)
                        .toArray(WordDistancePair[]::new);
    }

    public WordDistancePair[] getSomeClosestToInput(String userInput, int length,
                    Map<String, String> aliases) {
        var loweredInput = userInput.toLowerCase();
        var pairsWithAliases = new ArrayList<WordDistancePair>();
        for (var alias : aliases.keySet()) {
            pairsWithAliases.add(
                            new WordDistancePair(aliases.get(alias), alias, Integer.MAX_VALUE));
        }
        for (var name : charactersNames) {
            pairsWithAliases.add(new WordDistancePair(name, name, Integer.MAX_VALUE));
        }
        for (var pair : pairsWithAliases) {
            pair.distance = applyComparing(loweredInput, pair.alias.toLowerCase());
        }
        return pairsWithAliases.stream().sorted(Comparator.comparingInt(s -> s.distance))
                        .limit(length).toArray(WordDistancePair[]::new);
        // .toArray(WordDistancePair[]::new);)
        // return namesWithAliases.stream()
        // .map(s -> new WordDistancePair(s,
        // applyComparing(s.toLowerCase(), loweredInput)))
        // .sorted(Comparator.comparingInt(s -> s.distance)).limit(length)
        // .toArray(WordDistancePair[]::new);

    }
}
