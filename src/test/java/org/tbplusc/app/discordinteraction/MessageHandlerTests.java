package org.tbplusc.app.discordinteraction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.tbplusc.app.util.EnvWrapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class MessageHandlerTests {
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

    @Before
    public void setUp() {
        messageHandler = new MessageHandler();
    }

    @Test
    public void testHandlingCommand() throws ExecutionException, InterruptedException {
        EnvWrapper.registerValue("DISCORD_PREFIX", "!");

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
}
