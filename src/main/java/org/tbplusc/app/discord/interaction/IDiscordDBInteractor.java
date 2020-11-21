package org.tbplusc.app.discord.interaction;

public interface IDiscordDBInteractor {
    public void setPrefix(String guildId, String prefix);

    public String getPrefix(String guildId);
}
