package org.tbplusc.app.telegram.interaction;

import org.apache.commons.lang3.NotImplementedException;
import org.tbplusc.app.message.processing.MessageSender;
import org.tbplusc.app.message.processing.WrappedMessage;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class WrappedTelegramMessage implements WrappedMessage {
    private final Message message;
    private final TelegramBoostioBot responder;
    public WrappedTelegramMessage(Message message, TelegramBoostioBot responder){
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
    public void respond(String text) {
        var msgCommand = new SendMessage(this.getServerId(), text);
        try {
            responder.execute(msgCommand);
        } catch (TelegramApiException e) {
            responder.getLogger().error("Can't respond to message " + message.getText());
        }
    }
}
