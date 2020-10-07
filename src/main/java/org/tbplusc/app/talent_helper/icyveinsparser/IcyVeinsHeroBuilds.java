package org.tbplusc.app.talent_helper.icyveinsparser;

import java.util.ArrayList;
import java.util.List;

public class IcyVeinsHeroBuilds {
    private final String heroName;
    private final List<IcyVeinsBuild> builds;

    public IcyVeinsHeroBuilds(String heroName, List<IcyVeinsBuild> builds) {
        this.heroName = heroName;
        this.builds = new ArrayList<>(builds);
    }

    public String getHeroName() {
        return heroName;
    }

    public List<IcyVeinsBuild> getBuilds() {
        return new ArrayList<>(builds);
    }

    @Override
    public String toString() {
        return heroName + ": " + builds;
    }
}