package org.tbplusc.app.telegram.interaction;

import org.tbplusc.app.message.processing.MessageSender;
import org.tbplusc.app.message.processing.WrappedMessage;

public class WrappedTelegramMessage implements WrappedMessage {
    @Override
    public MessageSender getSender() {
        return MessageSender.telegram;
    }

    @Override
    public String getContextKey() {
        return null;
    }

    @Override
    public String getContent() {
        return null;
    }

    @Override
    public void respond(String text) {

    }
}
