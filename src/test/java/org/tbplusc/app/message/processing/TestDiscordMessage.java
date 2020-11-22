package org.tbplusc.app.message.processing;

import java.util.function.Consumer;
import org.apache.commons.lang3.NotImplementedException;

public class TestDiscordMessage implements WrappedMessage {

    private final Consumer<String> callback;
    private final String content;

    @Override
    public MessageSender getSender() {
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
    public void respond(String text) {
        callback.accept(text);
    }

    @Override
    public String getServerId() {
        throw new NotImplementedException();
    }
}
