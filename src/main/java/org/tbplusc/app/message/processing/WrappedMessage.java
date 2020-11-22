package org.tbplusc.app.message.processing;

public interface WrappedMessage {
    String getConversationId();

    String getServerId();

    String getContent();

    void respond(String text);
}
