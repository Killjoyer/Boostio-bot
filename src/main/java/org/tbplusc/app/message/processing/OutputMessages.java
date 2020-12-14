package org.tbplusc.app.message.processing;

import org.tbplusc.app.talent.helper.HeroBuild;

public class OutputMessages {
    public static String showHeroBuildToDiscord(HeroBuild build, StringBuilder talents) {
        return String.format("**%s**: ```md\n%s```**Description:** %s", build.getName(),
                talents, build.getDescription());
    }

    public static String showHeroBuildToTelegram(HeroBuild build, StringBuilder talents) {
        return String.format("%s: \n%s Description: %s", build.getName(), talents, build.getDescription());
    }
}
