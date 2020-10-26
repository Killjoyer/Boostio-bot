package org.tbplusc.app.discord.interaction;

public interface WrappedMessage {
    String getContextKey();

    String getContent();

    void respond(String text);
}
