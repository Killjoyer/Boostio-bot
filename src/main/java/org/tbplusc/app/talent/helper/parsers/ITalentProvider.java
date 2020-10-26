package org.tbplusc.app.talent.helper.parsers;

import java.io.IOException;

import org.tbplusc.app.talent.helper.HeroBuilds;

public interface ITalentProvider {
    public HeroBuilds getBuilds(String heroName) throws IOException;
}
