package org.tbplusc.app.talent.helper;

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
