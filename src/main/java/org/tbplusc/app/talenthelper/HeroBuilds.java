package org.tbplusc.app.talenthelper;

import java.util.ArrayList;
import java.util.List;

public class HeroBuilds {
    private final String heroName;
    private final List<HeroBuild> builds;

    public HeroBuilds(String heroName, List<HeroBuild> builds) {
        this.heroName = heroName;
        this.builds = new ArrayList<>(builds);
    }

    public String getHeroName() {
        return heroName;
    }

    public List<HeroBuild> getBuilds() {
        return new ArrayList<>(builds);
    }

    @Override
    public String toString() {
        return heroName + ": " + builds;
    }
}
