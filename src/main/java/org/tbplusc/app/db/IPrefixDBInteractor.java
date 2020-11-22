package org.tbplusc.app.db;

public interface IPrefixDBInteractor {
    public void setPrefix(String guildId, String prefix);

    public String getPrefix(String guildId);
}
