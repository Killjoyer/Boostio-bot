package org.tbplusc.app.validator;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Arrays;
import java.util.Comparator;


public class Validator {
    private final String[] _charactersNames;
    private final LevenshteinDistance levenshteinComparer = new LevenshteinDistance();

    public Validator(){
        _charactersNames = new String[]{"Mei", "Deathwing", "Qhira", "Anduin", "Imperius", "Orphea", "Mal'Ganis", "Mephisto",
                "Whitemane", "Yrel", "Deckard", "Fenix", "Maiev", "Blaze", "Hanzo", "Alexstrasza", "Junkrat",
                "Ana", "Kel'Thuzad", "Garrosh", "Stukov", "Malthael", "D.Va", "Genji", "Cassia", "Probius",
                "Lucio", "Valeera", "Zul'jin", "Ragnaros", "Varian", "Samuro", "Zarya", "Alarak", "Auriel",
                "Gul'dan", "Medivh", "Chromie", "Tracer", "Dehaka", "Xul", "Li-Ming", "Greymane", "Lunara", "Cho",
                "Gall", "Artanis", "Lt. Morales", "Rexxar", "Kharazim", "Leoric", "The Butcher", "Johanna", "Kael'thas",
                "Sylvanas", "The Lost Vikings", "Thrall", "Jaina", "Azmodan", "Anub'arak", "Chen", "Rehgar", "Zagara",
                "Murky", "Brightwing", "Li Li", "Tychus", "Abathur", "Arthas", "Diablo", "Illidan", "Kerrigan",
                "Malfurion", "Muradin", "Nazeebo", "Nova", "Raynor", "Sgt. Hammer", "Sonya", "Tyrael", "Tyrande",
                "Uther", "Valla", "Zeratul", "E.T.C.", "Falstad", "Gazlowe", "Stitches", "Tassadar"};
    }

    public String getClosestToInput(String userInput){
        var loweredInput = userInput.toLowerCase();
        return Arrays.stream(_charactersNames)
                .min(Comparator.comparing(t -> levenshteinComparer.apply(t.toLowerCase(), loweredInput)))
                .get();
    }
}
