package org.tbplusc.app.db;

import java.util.Map;

public interface IAliasesDBInteractor {
    public void addAlias(String serverId, String alias, String hero);

    public void removeAlias(String serverId, String alias);

    public Map<String, String> getAliases(String serverId);

    public String getHeroByAlias(String serverId, String alias);
}
