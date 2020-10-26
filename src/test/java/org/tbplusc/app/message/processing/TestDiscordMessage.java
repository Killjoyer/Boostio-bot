package org.tbplusc.app.message.processing;

import java.util.function.Consumer;

public class TestDiscordMessage implements WrappedMessage {

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
