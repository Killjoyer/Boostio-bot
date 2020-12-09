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
                        .thenReturn(new HeroBuilds("test", List.of(new HeroBuild("notcached",
                                        "main", List.of("a", "b", "c", "d", "CHECK", "f", "g")))));
        Mockito.when(buildDBCacher.getBuilds("test"))
                        .thenReturn(new HeroBuilds("test", List.of(new HeroBuild("cached", "main",
                                        List.of("a", "b", "c", "d", "CHECK", "f", "g")))));

        var results = new ArrayList<String>();
        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "!build test"));

        Assert.assertFalse(results.get(1).contains("notcached"));
        Assert.assertTrue(results.get(1).contains("cached"));
    }

    @Test
    public void testEcho() {
        var results = new ArrayList<String>();
        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "!echo KEKLOLARBIDOL"));

        Assert.assertEquals(results.get(0), "KEKLOLARBIDOL");
    }
}
