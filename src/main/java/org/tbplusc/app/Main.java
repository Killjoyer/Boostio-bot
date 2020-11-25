package org.tbplusc.app;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.discord.interaction.DiscordInitializer;
import org.tbplusc.app.message.processing.DefaultChatState;
import org.tbplusc.app.message.processing.MessageHandler;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;
import org.tbplusc.app.talent.helper.parsers.IcyVeinsRemoteDataProvider;
import org.tbplusc.app.talent.helper.parsers.IcyVeinsTalentProvider;
import org.tbplusc.app.telegram.interaction.TelegramInitializer;
import org.tbplusc.app.util.EnvWrapper;
import org.tbplusc.app.util.JsonDeserializer;
import org.tbplusc.app.validator.Validator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private interface Action<T1, T2> {
        void invoke(T1 t1, T2 t2);
    }

    private static final Map<String, Action<MessageHandler, Logger>> messengers = new HashMap<>() {
        {
            put("Discord", DiscordInitializer::initialize);
            // put("Telegram", TelegramInitializer::initialize);
        }
    };

    public static void main(String[] args) {
        logger.info("Application started");
        registerEnvVariables();
        final MessageHandler messageHandler;
        try {
            messageHandler = createMessageHandler();
        } catch (IOException e) {
            logger.error("Can't create message handler", e);
            return;
        }
        logger.info("Message handler is ready");
        var messengerThreads = startMessengers(messageHandler);
    }

    private static Validator createValidator() throws IOException {
        final var heroes = JsonDeserializer.deserializeHeroList(org.tbplusc.app.util.HttpGetter
                        .getBodyFromUrl("https://hotsapi.net/api/v1/heroes"));
        return new Validator(Arrays
                        .asList(heroes.stream().map((hero) -> hero.name).toArray(String[]::new)));
    }

    private static ITalentProvider createIcyVeinsTalentProvider() {
        return new IcyVeinsTalentProvider(new IcyVeinsRemoteDataProvider());
    }

    private static MessageHandler createMessageHandler() throws IOException {
        var defaultChatState = new DefaultChatState(null, null);
        DefaultChatState.registerDefaultCommands(defaultChatState, createValidator(),
                        createIcyVeinsTalentProvider(), null, null);
        return new MessageHandler(defaultChatState);
    }

    private static void registerEnvVariables() {
        EnvWrapper.registerValue("DISCORD_TOKEN", System.getenv("DISCORD_TOKEN"));
        EnvWrapper.registerValue("DISCORD_PREFIX", System.getenv("DISCORD_PREFIX"));
        EnvWrapper.registerValue("TELEGRAM_TOKEN", System.getenv("TELEGRAM_TOKEN"));
    }

    private static List<Thread> startMessengers(MessageHandler messageHandler) {
        var logger = Main.logger;
        List<Thread> threads = new ArrayList<>(Collections.emptyList());
        for (var messenger : messengers.entrySet()) {
            try {
                var th = new Thread(() -> messenger.getValue().invoke(messageHandler, logger));
                th.setName(messenger.getKey());
                threads.add(th);
                th.start();
                logger.info("Started thread for " + messenger.getKey());
            } catch (Exception e) {
                logger.error("Error occurred while initializing " + messenger.getKey(), e);
            }
        }
        logger.info("Stopped initializing threads");
        return threads;
    }
}
