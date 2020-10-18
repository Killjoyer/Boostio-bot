package org.tbplusc.app.talenthelper.parsers;

import java.io.IOException;
import org.tbplusc.app.talenthelper.HeroBuilds;

public interface ITalentProvider {
    public HeroBuilds getBuilds(String heroName) throws IOException;
}
