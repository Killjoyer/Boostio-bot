package org.tbplusc.app.talenthelper.parsers;

import java.io.IOException;
import java.util.ArrayList;

import org.tbplusc.app.talenthelper.HeroBuild;
import org.tbplusc.app.talenthelper.HeroBuilds;

public class IcyVeinsTalentProvider implements ITalentProvider {
    private final IIcyVeinsDataProvider dataProvider;

    public IcyVeinsTalentProvider(IIcyVeinsDataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    public HeroBuilds getBuilds(String heroName) throws IOException {
        var document = this.dataProvider.getDocument(heroName);
        var hero = document.getElementsByTag("h1").first().text();
        var outputBuilds = new ArrayList<HeroBuild>();
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
            outputBuilds.add(new HeroBuild(buildName, buildDesc, talents));
        }

        return new HeroBuilds(hero, outputBuilds);

    }

    private String normalizeHeroName(String heroName) {
        return heroName.replace(".", "").replace(" ", "-").replace("'", "").toLowerCase();
    }
}
