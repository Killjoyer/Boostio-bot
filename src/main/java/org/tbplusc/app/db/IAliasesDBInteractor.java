package org.tbplusc.app.db;

import java.util.Map;

public interface IAliasesDBInteractor {
    void addAlias(String serverId, String alias, String hero) throws FailedWriteException;

    void removeAlias(String serverId, String alias) throws FailedWriteException;

    Map<String, String> getAliases(String serverId) throws FailedReadException;

    String getHeroByAlias(String serverId, String alias) throws FailedReadException;
}
