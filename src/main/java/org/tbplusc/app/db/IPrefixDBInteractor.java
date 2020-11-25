package org.tbplusc.app.db;

public interface IPrefixDBInteractor {
    public void setPrefix(String serverId, String prefix);

    public String getPrefix(String serverId);
}
