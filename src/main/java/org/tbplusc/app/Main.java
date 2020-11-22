package org.tbplusc.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.discord.interaction.DiscordInitializer;
import org.tbplusc.app.message.processing.DefaultChatState;
import org.tbplusc.app.message.processing.MessageHandler;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;
import org.tbplusc.app.talent.helper.parsers.IcyVeinsRemoteDataProvider;
import org.tbplusc.app.talent.helper.parsers.IcyVeinsTalentProvider;
import org.tbplusc.app.util.EnvWrapper;
import org.tbplusc.app.util.JsonDeserializer;
import org.tbplusc.app.validator.Validator;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

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
        var discordEntity = new DiscordInitializer(messageHandler, logger);
    }

    private static Validator createValidator() throws IOException {
        final var heroes = JsonDeserializer.deserializeHeroList(org.tbplusc.app.util.HttpGetter
                        .getBodyFromUrl("https://hotsapi.net/api/v1/heroes"));
        return new Validator(Arrays.asList(heroes.stream().map((hero) -> hero.name)
                        .toArray(String[]::new)));
    }

    private static ITalentProvider createIcyVeinsTalentProvider() {
        return new IcyVeinsTalentProvider(new IcyVeinsRemoteDataProvider());
    }

    private static MessageHandler createMessageHandler() throws IOException {
        DefaultChatState.registerDefaultCommands(createValidator(), createIcyVeinsTalentProvider());
        return new MessageHandler();
    }

    private static void registerEnvVariables() {
        EnvWrapper.registerValue("DISCORD_TOKEN", System.getenv("DISCORD_TOKEN"));
        EnvWrapper.registerValue("DISCORD_PREFIX", System.getenv("DISCORD_PREFIX"));
        EnvWrapper.registerValue("TELEGRAM_TOKEN", System.getenv("DISCORD_PREFIX"));
    }
}
