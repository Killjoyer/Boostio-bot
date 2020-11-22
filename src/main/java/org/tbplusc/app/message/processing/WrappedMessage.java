package org.tbplusc.app.message.processing;

public interface WrappedMessage {
    MessageSender getSender();

    String getContextKey();

    String getContent();

    void respond(String text);
}
