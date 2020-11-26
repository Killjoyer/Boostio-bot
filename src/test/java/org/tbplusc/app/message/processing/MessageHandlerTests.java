package org.tbplusc.app.message.processing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.tbplusc.app.db.IAliasesDBInteractor;
import org.tbplusc.app.db.IPrefixDBInteractor;
import org.tbplusc.app.util.EnvWrapper;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class MessageHandlerTests {

    private IAliasesDBInteractor aliasesDBInteractorMock = Mockito.mock(IAliasesDBInteractor.class);
    private IPrefixDBInteractor prefixDBInteractoMock = Mockito.mock(IPrefixDBInteractor.class);
    private DefaultChatState defaultChatState =
                    new DefaultChatState(aliasesDBInteractorMock, prefixDBInteractoMock);

    private MessageHandler messageHandler = new MessageHandler(defaultChatState);

    @Before
    public void setUp() {
        EnvWrapper.registerValue("DISCORD_PREFIX", "!");
        Mockito.when(prefixDBInteractoMock.getPrefix("BB")).thenReturn("!");
    }

    @Test
    public void testHandlingCommand() throws ExecutionException, InterruptedException {
        var called = new AtomicBoolean(false);
        DefaultChatState.registerCommand("test", (args, message) -> {
            called.set(true);
            message.respond("TEST");
            return defaultChatState;
        });

        var messageStub = new TestDiscordMessage((text) -> Assert.assertEquals(text, "TEST"),
                        "!test");

        messageHandler.handleMessage(messageStub).get();


        Assert.assertTrue(called.get());
    }

    @Test
    public void testIgnoreNonCommands() throws ExecutionException, InterruptedException {
        var results = new ArrayList<String>();
        messageHandler.handleMessage(new TestDiscordMessage(results::add, "KEKLOLARBIDOL")).get();

        Assert.assertEquals(0, results.size());
    }

    @Test
    public void testWrongCommand() throws ExecutionException, InterruptedException {
        var results = new ArrayList<String>();
        messageHandler.handleMessage(new TestDiscordMessage(results::add, "!keklolarbidol7623765"))
                        .get();

        Assert.assertEquals(0, results.size());
    }
}
