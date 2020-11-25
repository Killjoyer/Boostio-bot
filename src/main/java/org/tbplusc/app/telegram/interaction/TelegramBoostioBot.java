package org.tbplusc.app.telegram.interaction;

import org.slf4j.Logger;
import org.tbplusc.app.message.processing.MessageHandler;
import org.tbplusc.app.util.EnvWrapper;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramBoostioBot extends TelegramLongPollingBot {
    private final String token = EnvWrapper.getValue("TELEGRAM_TOKEN");
    private final MessageHandler messageHandler;
    private final Logger logger;

    public TelegramBoostioBot(MessageHandler messageHandler, Logger logger) {
        this.messageHandler = messageHandler;
        this.logger = logger;
    }

    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public String getBotUsername() {
        return "boostio_bot";
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            messageHandler.handleMessage(new WrappedTelegramMessage(update.getMessage(), this));
        }
    }
}
