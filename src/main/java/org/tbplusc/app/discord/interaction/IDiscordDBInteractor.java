package org.tbplusc.app.discord.interaction;

public interface IDiscordDBInteractor {
    public void initSettings();

    public void changePrefix(String prefix);

    public void addAlias(String alias);

    public void removeAlias(String alias);

    public String getPrefix();

    public DiscordSettings getSettings();
}
