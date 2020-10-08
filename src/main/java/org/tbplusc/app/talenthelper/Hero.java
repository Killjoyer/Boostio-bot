package org.tbplusc.app.talenthelper;

import java.util.List;

public class Hero {
    public String name;
    public String shortName;
    public List<String> translations;

    @Override
    public String toString() {
        return name + ": " + translations;
    }
}
