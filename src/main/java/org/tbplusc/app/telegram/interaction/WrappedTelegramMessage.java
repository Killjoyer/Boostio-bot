package org.tbplusc.app.telegram.interaction;

import org.tbplusc.app.message.processing.MessageSender;
import org.tbplusc.app.message.processing.WrappedMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class WrappedTelegramMessage implements WrappedMessage {
    private final Message message;
    private final TelegramBoostioBot responder;

    public WrappedTelegramMessage(Message message, TelegramBoostioBot responder) {
        this.message = message;
        this.responder = responder;
    }

    @Override
    public MessageSender getSenderApp() {
        return MessageSender.telegram;
    }

    @Override
    public String getConversationId() {
        return message.getFrom().getUserName() + this.getServerId();
    }

    @Override
    public String getServerId() {
        return Long.toString(message.getChatId());
    }

    @Override
    public String getContent() {
        return message.getText();
    }

    @Override
    public WrappedTelegramBotRespondMessage respond(String text) {
        var msgCommand = new SendMessage(this.getServerId(), text);
        try {
            return new WrappedTelegramBotRespondMessage(this, responder.execute(msgCommand), responder);
        } catch (TelegramApiException e) {
            responder.getLogger().error("Can't respond to message " + message.getText());
        }
        return null;
    }
}
