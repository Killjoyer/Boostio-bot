package org.tbplusc.app.message.processing;

public interface WrappedMessage {
    MessageSender getSenderApp();

    String getConversationId();

    String getServerId();
  
    String getContent();

    void respond(String text);
}
