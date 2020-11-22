package org.tbplusc.app.message.processing;

public interface WrappedMessage {
    MessageSender getSender();

    String getConversationId();

    String getServerId();
  
    String getContent();

    void respond(String text);
}
