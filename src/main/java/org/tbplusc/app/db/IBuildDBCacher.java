package org.tbplusc.app.db;

import org.tbplusc.app.talent.helper.HeroBuilds;

public interface IBuildDBCacher {
    HeroBuilds getBuilds(String heroName) throws FailedReadException;

    void cacheBuilds(String heroName, HeroBuilds builds) throws FailedWriteException;

    void clearCache() throws FailedWriteException;
}
