package org.tbplusc.app.message.processing;

import org.jvnet.hk2.annotations.Optional;

public interface WrappedMessage {
    MessageSender getSenderApp();

    String getConversationId();

    String getServerId();

    String getContent();

    WrappedBotRespondMessage respond(String text, boolean keyboarded);

    default WrappedBotRespondMessage respond(String text) {
        return respond(text, false);
    }
}
