package org.tbplusc.app.discordinteraction;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class MessageHandlerTests {
    private MessageHandler messageHandler;

    @Before
    public void setUp() {
        messageHandler = new MessageHandler();
    }

    @Test
    public void testHandlingCommand() {
        var called = new AtomicBoolean(false);
        DefaultChatState.registerCommand("test", (args, message) -> {
            called.set(true);
            return new DefaultChatState();
        });


        Assert.assertTrue(called.get());
    }
}
