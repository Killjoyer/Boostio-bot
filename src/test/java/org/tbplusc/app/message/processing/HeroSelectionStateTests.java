package org.tbplusc.app.message.processing;

import org.junit.Test;
import org.mockito.Mockito;
import org.tbplusc.app.db.FailedReadException;
import org.tbplusc.app.db.FailedWriteException;
import org.tbplusc.app.db.IBuildDBCacher;
import org.tbplusc.app.talent.helper.HeroBuild;
import org.tbplusc.app.talent.helper.HeroBuilds;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HeroSelectionStateTests {
    @Test
    public void testCacheEmpty() throws FailedWriteException, FailedReadException, IOException {
        var cacher = Mockito.mock(IBuildDBCacher.class);
        var builds = new HeroBuilds("test", List.of(new HeroBuild("puredata", "main",
                        List.of("a", "b", "c", "d", "CHECK", "f", "g"))));
        Mockito.when(cacher.getBuilds("lucio")).thenThrow(FailedReadException.class);
        var talentHelperMock = Mockito.mock(ITalentProvider.class);
        Mockito.when(talentHelperMock.getBuilds("lucio")).thenReturn(builds);
        HeroSelectionState.showHeroBuildInMarkdown(
                        new TestDiscordMessage((response) -> {}, "!build lucio"),
                        "lucio",
                        talentHelperMock,
                        cacher
        );
        Mockito.verify(cacher, Mockito.times(1)).cacheBuilds("lucio", builds);
    }

    @Test
    public void testCacheAvailible() throws FailedReadException, IOException, FailedWriteException {
        var cacher = Mockito.mock(IBuildDBCacher.class);
        var builds = new HeroBuilds("test", List.of(new HeroBuild("puredata", "main",
                        List.of("a", "b", "c", "d", "CHECK", "f", "g"))));
        Mockito.when(cacher.getBuilds("lucio")).thenReturn(builds);
        var talentHelperMock = Mockito.mock(ITalentProvider.class);
        HeroSelectionState.showHeroBuildInMarkdown(
                        new TestDiscordMessage((response) -> {}, "!build lucio"),
                        "lucio",
                        talentHelperMock,
                        cacher
        );
        Mockito.verify(cacher, Mockito.never()).cacheBuilds("lucio", builds);
        Mockito.verify(talentHelperMock, Mockito.never()).getBuilds("lucio");
    }
}
