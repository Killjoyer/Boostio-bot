package org.tbplusc.app.discordinteraction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.tbplusc.app.talenthelper.HeroBuild;
import org.tbplusc.app.talenthelper.HeroBuilds;
import org.tbplusc.app.talenthelper.parsers.ITalentProvider;
import org.tbplusc.app.util.EnvWrapper;
import org.tbplusc.app.validator.Validator;
import org.tbplusc.app.validator.WordDistancePair;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MessageHandlerTests {
    private Validator validatorMock;
    private ITalentProvider talentHelperMock;


    private static class TestDiscordMessage implements WrappedMessage {

        private final Consumer<String> callback;
        private final String content;

        public TestDiscordMessage(Consumer<String> callback, String content) {
            this.callback = callback;
            this.content = content;
        }

        @Override public String getContextKey() {
            return "AA";
        }

        @Override public String getContent() {
            return content;
        }

        @Override public void respond(String text) {
            callback.accept(text);
        }
    }


    private MessageHandler messageHandler;

    @Before public void setUp() {
        messageHandler = new MessageHandler();
        validatorMock = Mockito.mock(Validator.class);
        talentHelperMock = Mockito.mock(ITalentProvider.class);
        DefaultChatState.registerDefaultCommands(validatorMock, talentHelperMock);
        EnvWrapper.registerValue("DISCORD_PREFIX", "!");
    }

    @Test public void testHandlingCommand() throws ExecutionException, InterruptedException {
        var called = new AtomicBoolean(false);
        DefaultChatState.registerCommand("test", (args, message) -> {
            called.set(true);
            message.respond("TEST");
            return new DefaultChatState();
        });

        var messageStub = new TestDiscordMessage((text) -> {
            Assert.assertEquals(text, "TEST");
        }, "!test");

        messageHandler.handleMessage(messageStub).get();


        Assert.assertTrue(called.get());
    }

    @Test public void testBuildsCommandWithExactMatch()
                    throws IOException, ExecutionException, InterruptedException {
        Mockito.when(validatorMock.getSomeClosestToInput("test", 10))
                        .thenReturn(new WordDistancePair[] {new WordDistancePair("test", 0),
                                        new WordDistancePair("test1", 1),});
        Mockito.when(talentHelperMock.getBuilds("test")).thenReturn(new HeroBuilds("test",
                        List.of(new HeroBuild("main", "main",
                                        List.of("a", "b", "c", "d", "CHECK", "f", "g")))));
        var results = new ArrayList<String>();
        messageHandler.handleMessage(new TestDiscordMessage(results::add, "!build test")).get();

        Assert.assertTrue(results.get(0).contains("test"));
        Assert.assertTrue(results.get(1).contains("main"));
        Assert.assertTrue(results.get(1).contains("CHECK"));
    }

    @Test public void testBuildsCommandWithNonExactMatch()
                    throws IOException, ExecutionException, InterruptedException {
        Mockito.when(validatorMock.getSomeClosestToInput("test1", 10))
                        .thenReturn(new WordDistancePair[] {new WordDistancePair("test2", 1),
                                        new WordDistancePair("test23", 2),});
        Mockito.when(talentHelperMock.getBuilds("test23")).thenReturn(new HeroBuilds("test23",
                        List.of(new HeroBuild("main", "main",
                                        List.of("a", "b", "c", "d", "CHECK", "f", "g")))));

        var firstResults = new ArrayList<String>();
        messageHandler.handleMessage(new TestDiscordMessage(firstResults::add, "!build test1"))
                        .get();

        Assert.assertTrue(firstResults.get(0).contains("test2"));
        Assert.assertTrue(firstResults.get(0).contains("test23"));

        var secondResults = new ArrayList<String>();
        messageHandler.handleMessage(new TestDiscordMessage(secondResults::add, "2")).get();

        Assert.assertTrue(secondResults.get(0).contains("test23"));
        Assert.assertTrue(secondResults.get(1).contains("CHECK"));
    }

    @Test public void testIgnoreNonCommands() throws ExecutionException, InterruptedException {
        var results = new ArrayList<String>();
        messageHandler.handleMessage(new TestDiscordMessage(results::add, "KEKLOLARBIDOL")).get();

        Assert.assertEquals(0, results.size());
    }

    @Test public void testEcho() throws ExecutionException, InterruptedException {
        var results = new ArrayList<String>();
        messageHandler.handleMessage(new TestDiscordMessage(results::add, "!echo KEKLOLARBIDOL"))
                        .get();

        Assert.assertEquals(results.get(0), "KEKLOLARBIDOL");
    }

    @Test public void testWrongCommand() throws ExecutionException, InterruptedException {
        var results = new ArrayList<String>();
        messageHandler.handleMessage(new TestDiscordMessage(results::add, "!keklolarbidol7623765"))
                        .get();

        Assert.assertEquals(0, results.size());
    }
}
