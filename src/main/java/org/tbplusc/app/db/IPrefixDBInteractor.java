package org.tbplusc.app.db;

public interface IPrefixDBInteractor {
    void setPrefix(String guildId, String prefix) throws FailedWriteException;

    String getPrefix(String guildId) throws FailedReadException;
}
