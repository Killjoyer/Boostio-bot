package org.tbplusc.app.db;

public interface IPrefixDBInteractor {
    void setPrefix(String serverId, String prefix) throws FailedWriteException;

    String getPrefix(String serverId) throws FailedReadException;
}
