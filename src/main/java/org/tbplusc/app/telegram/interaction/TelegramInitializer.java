package org.tbplusc.app.telegram.interaction;

import org.slf4j.Logger;
import org.tbplusc.app.message.processing.MessageHandler;
import org.tbplusc.app.util.EnvWrapper;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramInitializer {
    public static final String token = EnvWrapper.getValue("TELEGRAM_TOKEN");

    public TelegramInitializer() {
        throw new IllegalStateException("Utility class");
    }

    public static void initialize(MessageHandler messageHandler, Logger logger) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            var telegramEntity = new TelegramBoostioBot();
            telegramEntity.setUp(messageHandler, logger);
            telegramBotsApi.registerBot(telegramEntity);
            logger.info("Telegram initialized");
        } catch (TelegramApiException e) {
            logger.error("Cannot create TG session");
            e.printStackTrace();
        }
    }
}
