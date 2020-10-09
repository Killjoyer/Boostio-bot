package org.tbplusc.app.talenthelper.icyveinsparser;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.tbplusc.app.util.HttpGetter;

public class IcyVeinsParser {
    private static final String ADDRESS_PREFIX = "https://www.icy-veins.com/heroes/";
    private static final String ADDRESS_POSTFIX = "-talents";

    private IcyVeinsParser() {
        throw new IllegalStateException("Utility class");
    }

    public static IcyVeinsHeroBuilds getBuildsByHeroName(String heroName) throws IOException {
        var talentPage = getDocumentFromHeroName(heroName);
        return new IcyVeinsHeroBuilds(heroName, getBuildsListFromDocument(talentPage));
    }

    public static List<IcyVeinsBuild> getBuildsListFromDocument(org.jsoup.nodes.Document document) {
        var outputBuilds = new ArrayList<IcyVeinsBuild>();
        var buildElems = document.getElementsByClass("heroes_builds").first()
                        .getElementsByClass("heroes_build");

        for (var build : buildElems) {
            var buildName = build.getElementsByClass("toc_no_parsing").first().text();
            var buildDesc = build.getElementsByClass("heroes_build_text").first().text();
            var talents = new ArrayList<String>();
            build.getElementsByClass("heroes_build_talent_tier").forEach(tier -> {
                var talent = tier.selectFirst("img").attr("alt");
                talent = talent.substring(0, talent.lastIndexOf(" "));
                talents.add(talent);
            });
            outputBuilds.add(new IcyVeinsBuild(buildName, buildDesc, talents));
        }

        return outputBuilds;

    }

    private static org.jsoup.nodes.Document getDocumentFromHeroName(String heroName)
                    throws IOException {
        return HttpGetter.getDocumentFromUrl(ADDRESS_PREFIX + heroName + ADDRESS_POSTFIX);
    }

}
