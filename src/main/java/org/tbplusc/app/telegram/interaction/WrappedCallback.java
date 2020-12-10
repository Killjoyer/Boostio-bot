package org.tbplusc.app.telegram.interaction;

import org.jvnet.hk2.annotations.Optional;
import org.tbplusc.app.message.processing.MessageSender;
import org.tbplusc.app.message.processing.WrappedMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.validation.constraints.NotNull;

public class WrappedCallback implements WrappedMessage {
    private final CallbackQuery callback;
    private final TelegramBoostioBot responder;

    public WrappedCallback(CallbackQuery callback, TelegramBoostioBot responder) {
        this.callback = callback;
        this.responder = responder;
    }

    @Override
    public MessageSender getSenderApp() {
        return MessageSender.telegram;
    }

    @Override
    public String getConversationId() {
        return callback.getFrom().getUserName() + this.getServerId();
    }

    @Override
    public String getServerId() {
        return Long.toString(callback.getMessage().getChatId());
    }

    @Override
    public String getContent() {
        return callback.getData();
    }

    @Override
    public WrappedTelegramBotRespondMessage respond(String text, boolean keyboarded) {
        try {
            var msgCommand = new SendMessage(this.getServerId(), text);
            msgCommand.enableMarkdown(true);
            if (keyboarded) msgCommand.setReplyMarkup(new NumbersKeyboard());
            return new WrappedTelegramBotRespondMessage(responder.execute(msgCommand), responder);
        } catch (TelegramApiException e) {
            responder.getLogger().error("Can't respond to message " + this.getContent(), e);
        }
        return null;
    }
}
