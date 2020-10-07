package org.tbplusc.app.talent_helper;

import java.util.List;

public class Hero {
    public String name;
    public String short_name;
    public List<String> translations;

    @Override
    public String toString() {
        return name + ": " + translations;
    }
}