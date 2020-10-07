package org.tbplusc.app.talent_helper.icyveinsparser;

import java.io.IOException;
import java.util.ArrayList;

import org.tbplusc.app.util.HTTPGetter;

public class IcyVeinsParser {
    private static final String ADDRESS_PREFIX = "https://www.icy-veins.com/heroes/";
    private static final String ADDRESS_POSTFIX = "-talents";

    private IcyVeinsParser() {
        throw new IllegalStateException("Utility class");
    }

    public static IcyVeinsHeroBuilds getBuildsByHeroName(String heroName) throws IOException {
        var outputBuilds = new ArrayList<IcyVeinsBuild>();
        var talentPage = HTTPGetter.getDocumentFromUrl(ADDRESS_PREFIX + heroName + ADDRESS_POSTFIX);

        var buildElems = talentPage.getElementsByClass("heroes_builds").first().getElementsByClass("heroes_build");
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

        return new IcyVeinsHeroBuilds(heroName, outputBuilds);
    }
}
