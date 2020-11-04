package org.tbplusc.app.discord.interaction;

import java.util.HashMap;
import java.util.Map;

public class DiscordServerSettings {
    private final String guildId;
    private final String prefix;
    private final Map<String, String> aliases;

    public DiscordServerSettings(String guildId, String prefix, Map<String, String> aliases) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.aliases = aliases;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getPrefix() {
        return prefix;
    }

    public Map<String, String> getAliases() {
        return new HashMap<String, String>(aliases);
    }
}
