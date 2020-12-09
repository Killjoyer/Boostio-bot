package org.tbplusc.app.message.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.tbplusc.app.db.FailedReadException;
import org.tbplusc.app.db.FailedWriteException;
import org.tbplusc.app.db.IAliasesDBInteractor;
import org.tbplusc.app.db.IBuildDBCacher;
import org.tbplusc.app.db.IPrefixDBInteractor;
import org.tbplusc.app.talent.helper.HeroBuild;
import org.tbplusc.app.talent.helper.HeroBuilds;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;
import org.tbplusc.app.util.EnvWrapper;
import org.tbplusc.app.validator.Validator;
import org.tbplusc.app.validator.WordDistancePair;

public class DefaultChatStateTests {
    private Validator validatorMock;
    private ITalentProvider talentHelperMock;
    private DefaultChatState defaultChatState;
    private IAliasesDBInteractor aliasesDBInteractorMock;
    private IPrefixDBInteractor prefixDBInteractorMock;
    private IBuildDBCacher buildDBCacher;
    private HashMap<String, String> aliases;

    @Before
    public void setUp() throws FailedReadException {
        validatorMock = Mockito.mock(Validator.class);
        talentHelperMock = Mockito.mock(ITalentProvider.class);
        aliasesDBInteractorMock = Mockito.mock(IAliasesDBInteractor.class);
        prefixDBInteractorMock = Mockito.mock(IPrefixDBInteractor.class);
        buildDBCacher = Mockito.mock(IBuildDBCacher.class);

        aliases = new HashMap<>() {
            {
                put("test", "test");
                put("test2", "test2");
                put("test23", "test23");
            }
        };

        defaultChatState = new DefaultChatState(aliasesDBInteractorMock, prefixDBInteractorMock);
        DefaultChatState.registerDefaultCommands(defaultChatState, validatorMock, talentHelperMock,
                        aliasesDBInteractorMock, prefixDBInteractorMock, buildDBCacher);

        Mockito.when(prefixDBInteractorMock.getPrefix("BB")).thenReturn("!");
        Mockito.when(aliasesDBInteractorMock.getAliases("BB")).thenReturn(aliases);

        EnvWrapper.registerValue("DISCORD_PREFIX", "!");
    }

    @Test
    public void testBuildsCommandWithExactMatch() throws IOException, FailedReadException {
        Mockito.when(validatorMock.getSomeClosestToInput("test", 10, aliases))
                        .thenReturn(new WordDistancePair[] {new WordDistancePair("test", "test", 0),
                            new WordDistancePair("test1", "test1", 1),});
        Mockito.when(talentHelperMock.getBuilds("test"))
                        .thenReturn(new HeroBuilds("test", List.of(new HeroBuild("main", "main",
                                        List.of("a", "b", "c", "d", "CHECK", "f", "g")))));
        Mockito.when(buildDBCacher.getBuilds("test")).thenThrow(new FailedReadException());

        var results = new ArrayList<String>();
        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "!build test"));

        Assert.assertTrue(results.get(0).contains("TEST"));
        Assert.assertTrue(results.get(1).contains("main"));
        Assert.assertTrue(results.get(1).contains("CHECK"));
    }

    @Test
    public void testBuildsCommandWithNonExactMatch() throws IOException, FailedReadException {
        Mockito.when(validatorMock.getSomeClosestToInput("test1", 10, aliases))
                        .thenReturn(new WordDistancePair[] {
                            new WordDistancePair("test2", "test2", 1),
                            new WordDistancePair("test23", "test23", 2),});
        Mockito.when(talentHelperMock.getBuilds("test23"))
                        .thenReturn(new HeroBuilds("test23", List.of(new HeroBuild("main", "main",
                                        List.of("a", "b", "c", "d", "CHECK", "f", "g")))));
        Mockito.when(buildDBCacher.getBuilds("test23")).thenThrow(new FailedReadException());

        var firstResults = new ArrayList<String>();
        var newState = defaultChatState
                        .handleMessage(new TestDiscordMessage(firstResults::add, "!build test1"));

        Assert.assertTrue(firstResults.get(0).contains("TEST2"));
        Assert.assertTrue(firstResults.get(0).contains("TEST23"));
        Assert.assertEquals(newState.getClass(), HeroSelectionState.class);

        var secondResults = new ArrayList<String>();
        newState.handleMessage(new TestDiscordMessage(secondResults::add, "2"));

        Assert.assertTrue(secondResults.get(0).contains("TEST23"));
        Assert.assertTrue(secondResults.get(1).contains("CHECK"));
    }

    @Test
    public void testCacheUsage() throws IOException, FailedReadException {
        Mockito.when(validatorMock.getSomeClosestToInput("test", 10, aliases))
                        .thenReturn(new WordDistancePair[] {new WordDistancePair("test", "test", 0),
                            new WordDistancePair("test1", "test1", 1),});
        Mockito.when(talentHelperMock.getBuilds("test"))
                        .thenReturn(new HeroBuilds("test", List.of(new HeroBuild("puredata", "main",
                                        List.of("a", "b", "c", "d", "CHECK", "f", "g")))));
        Mockito.when(buildDBCacher.getBuilds("test"))
                        .thenReturn(new HeroBuilds("test", List.of(new HeroBuild("cached", "main",
                                        List.of("a", "b", "c", "d", "CHECK", "f", "g")))));

        var resultsWhenCached = new ArrayList<String>();
        defaultChatState.handleMessage(
                        new TestDiscordMessage(resultsWhenCached::add, "!build test"));

        Assert.assertFalse(resultsWhenCached.get(1).contains("puredata"));
        Assert.assertTrue(resultsWhenCached.get(1).contains("cached"));

        var resultsWhenCacheIsCleared = new ArrayList<String>();
        defaultChatState.handleMessage(
                        new TestDiscordMessage(resultsWhenCacheIsCleared::add, "!clear-cache"));
        Assert.assertEquals(resultsWhenCacheIsCleared.get(0), "Cache was successfully cleared");
        Mockito.when(buildDBCacher.getBuilds("test")).thenThrow(new FailedReadException());
        defaultChatState.handleMessage(
                        new TestDiscordMessage(resultsWhenCacheIsCleared::add, "!build test"));
        Assert.assertFalse(resultsWhenCacheIsCleared.get(2).contains("cached"));
        Assert.assertTrue(resultsWhenCacheIsCleared.get(2).contains("puredata"));
    }

    @Test
    public void testPrefixOperations() throws FailedReadException, FailedWriteException {
        Mockito.when(prefixDBInteractorMock.getPrefix("BB")).thenReturn("!");
        var results = new ArrayList<String>();

        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "!echo kek"));
        Assert.assertEquals(results.get(0), "kek");
        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "!prefix ^"));
        Mockito.when(prefixDBInteractorMock.getPrefix("BB")).thenReturn("^");

        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "!echo test"));
        Assert.assertTrue(results.size() == 1);

        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "^echo test"));
        Assert.assertEquals(results.get(1), "test");
    }

    @Test
    public void testAliasOperations()
                    throws IOException, FailedReadException, FailedWriteException {
        Mockito.when(validatorMock.getSomeClosestToInput("tes", 10, aliases))
                        .thenReturn(new WordDistancePair[] {new WordDistancePair("test", "test", 1),
                            new WordDistancePair("test1", "test1", 2),});
        Mockito.when(talentHelperMock.getBuilds("test"))
                        .thenReturn(new HeroBuilds("test", List.of(new HeroBuild("notcached",
                                        "main", List.of("a", "b", "c", "d", "CHECK", "f", "g")))));
        Mockito.when(buildDBCacher.getBuilds("test")).thenThrow(new FailedReadException());

        var resultsWithoutAlias = new ArrayList<String>();
        var stateWithoutAlias = defaultChatState.handleMessage(
                        new TestDiscordMessage(resultsWithoutAlias::add, "!build tes"));
        stateWithoutAlias.handleMessage(new TestDiscordMessage(resultsWithoutAlias::add, "1"));
        Assert.assertTrue(resultsWithoutAlias.get(2).contains("CHECK"));

        var resultsWithNewAlias = new ArrayList<String>();
        defaultChatState.handleMessage(
                        new TestDiscordMessage(resultsWithNewAlias::add, "!alias tes test"));
        aliases.put("tes", "test");
        Mockito.when(validatorMock.getSomeClosestToInput("tes", 10, aliases))
                        .thenReturn(new WordDistancePair[] {new WordDistancePair("test", "tes", 0),
                            new WordDistancePair("test", "test", 1),
                            new WordDistancePair("test1", "test1", 2),});
        defaultChatState.handleMessage(
                        new TestDiscordMessage(resultsWithNewAlias::add, "!build tes"));
        Assert.assertTrue(resultsWithNewAlias.get(1).contains("CHECK"));

        defaultChatState.handleMessage(
                        new TestDiscordMessage(resultsWithoutAlias::add, "!rmv-alias tes"));
        aliases.remove("tes");
        Mockito.when(validatorMock.getSomeClosestToInput("tes", 10, aliases))
                        .thenReturn(new WordDistancePair[] {new WordDistancePair("test", "test", 1),
                            new WordDistancePair("test1", "test1", 2),});
        stateWithoutAlias = defaultChatState.handleMessage(
                        new TestDiscordMessage(resultsWithoutAlias::add, "!build tes"));
        stateWithoutAlias.handleMessage(new TestDiscordMessage(resultsWithoutAlias::add, "1"));
        Assert.assertTrue(resultsWithoutAlias.get(5).contains("CHECK"));
    }

    @Test
    public void testEcho() {
        var results = new ArrayList<String>();
        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "!echo KEKLOLARBIDOL"));

        Assert.assertEquals(results.get(0), "KEKLOLARBIDOL");
    }
}
