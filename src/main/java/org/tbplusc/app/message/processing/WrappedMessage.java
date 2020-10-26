package org.tbplusc.app.message.processing;

public interface WrappedMessage {
    String getContextKey();

    String getContent();

    void respond(String text);
}
