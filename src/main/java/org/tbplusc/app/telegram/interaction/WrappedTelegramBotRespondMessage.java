package org.tbplusc.app.telegram.interaction;

import org.tbplusc.app.message.processing.WrappedBotRespondMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class WrappedTelegramBotRespondMessage implements WrappedBotRespondMessage {
    private final Message message;
    private final WrappedTelegramMessage prevMessage;
    private final TelegramBoostioBot responder;

    public WrappedTelegramBotRespondMessage(WrappedTelegramMessage prevMessage, Message message, TelegramBoostioBot responder) {
        this.message = message;
        this.responder = responder;
        this.prevMessage = prevMessage;
    }

    @Override
    public void delete() {
        var msgCommand = new DeleteMessage(prevMessage.getServerId(), message.getMessageId());
        try {
            responder.execute(msgCommand);
        } catch (TelegramApiException e) {
            responder.getLogger().error("Can't delete message " + message.getText());
        }
    }
}
