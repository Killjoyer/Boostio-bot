package org.tbplusc.app.message.processing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.tbplusc.app.talent.helper.HeroBuild;
import org.tbplusc.app.talent.helper.HeroBuilds;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;
import org.tbplusc.app.util.EnvWrapper;
import org.tbplusc.app.validator.Validator;
import org.tbplusc.app.validator.WordDistancePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultChatStateTests {
    private Validator validatorMock;
    private ITalentProvider talentHelperMock;
    private DefaultChatState defaultChatState;

    @Before public void setUp() {
        validatorMock = Mockito.mock(Validator.class);
        talentHelperMock = Mockito.mock(ITalentProvider.class);
        DefaultChatState.registerDefaultCommands(validatorMock, talentHelperMock, null, null);
        EnvWrapper.registerValue("DISCORD_PREFIX", "!");
        defaultChatState = new DefaultChatState();
    }

    @Test public void testBuildsCommandWithExactMatch() throws IOException {
        Mockito.when(validatorMock.getSomeClosestToInput("test", 10))
                        .thenReturn(new WordDistancePair[] {new WordDistancePair("test", 0),
                                        new WordDistancePair("test1", 1),});
        Mockito.when(talentHelperMock.getBuilds("test")).thenReturn(new HeroBuilds("test",
                        List.of(new HeroBuild("main", "main",
                                        List.of("a", "b", "c", "d", "CHECK", "f", "g")))));
        var results = new ArrayList<String>();
        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "!build test"));

        Assert.assertTrue(results.get(0).contains("test"));
        Assert.assertTrue(results.get(1).contains("main"));
        Assert.assertTrue(results.get(1).contains("CHECK"));
    }

    @Test public void testBuildsCommandWithNonExactMatch() throws IOException {
        Mockito.when(validatorMock.getSomeClosestToInput("test1", 10))
                        .thenReturn(new WordDistancePair[] {new WordDistancePair("test2", 1),
                                        new WordDistancePair("test23", 2),});
        Mockito.when(talentHelperMock.getBuilds("test23")).thenReturn(new HeroBuilds("test23",
                        List.of(new HeroBuild("main", "main",
                                        List.of("a", "b", "c", "d", "CHECK", "f", "g")))));

        var firstResults = new ArrayList<String>();
        var newState = defaultChatState.handleMessage(new TestDiscordMessage(firstResults::add, "!build test1"));

        Assert.assertTrue(firstResults.get(0).contains("test2"));
        Assert.assertTrue(firstResults.get(0).contains("test23"));
        Assert.assertEquals(newState.getClass(), HeroSelectionState.class);

        var secondResults = new ArrayList<String>();
        newState.handleMessage(new TestDiscordMessage(secondResults::add, "2"));

        Assert.assertTrue(secondResults.get(0).contains("test23"));
        Assert.assertTrue(secondResults.get(1).contains("CHECK"));
    }

    @Test public void testEcho() {
        var results = new ArrayList<String>();
        defaultChatState.handleMessage(new TestDiscordMessage(results::add, "!echo KEKLOLARBIDOL"));

        Assert.assertEquals(results.get(0), "KEKLOLARBIDOL");
    }
}
