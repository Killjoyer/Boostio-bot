package org.tbplusc.app.discordinteraction;

public interface WrappedMessage {
    String getContextKey();

    String getContent();

    void respond(String text);
}
