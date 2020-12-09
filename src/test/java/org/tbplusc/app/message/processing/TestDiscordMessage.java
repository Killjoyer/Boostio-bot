package org.tbplusc.app.message.processing;

import java.util.function.Consumer;
import org.mockito.Mockito;

public class TestDiscordMessage implements WrappedMessage {

    private final Consumer<String> callback;
    private final String content;

    @Override
    public MessageSender getSenderApp() {
        return MessageSender.discord;
    }

    public TestDiscordMessage(Consumer<String> callback, String content) {
        this.callback = callback;
        this.content = content;
    }

    @Override
    public String getConversationId() {
        return "AA";
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public WrappedBotRespondMessage respond(String text, boolean keyboarded) {
        callback.accept(text);
        return Mockito.mock(WrappedBotRespondMessage.class);
    }

    @Override
    public String getServerId() {
        return "BB";
    }
}
