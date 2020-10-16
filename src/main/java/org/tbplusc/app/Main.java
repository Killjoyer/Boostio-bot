package org.tbplusc.app;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.discordinteraction.DefaultChatState;
import org.tbplusc.app.discordinteraction.MessageHandler;
import org.tbplusc.app.talenthelper.parsers.ITalentProvider;
import org.tbplusc.app.talenthelper.parsers.IcyVeinsRemoteDataProvider;
import org.tbplusc.app.talenthelper.parsers.IcyVeinsTalentProvider;
import org.tbplusc.app.util.JsonDeserializer;
import org.tbplusc.app.validator.Validator;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Application started");
        final var token = System.getenv("DISCORD_TOKEN");
        final var client = DiscordClient.create(token);
        final MessageHandler messageHandler;
        try {
            messageHandler = createMessageHandler();
        } catch (IOException e) {
            logger.error("Can't create message handler", e);
            return;
        }
        logger.info("Message handler is ready");
        final var gateway = client.login().block();
        if (gateway == null) {
            logger.error("Can't connect to discord");
            return;
        }
        gateway.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
                        .subscribe(messageHandler::handleMessage);
        gateway.on(DisconnectEvent.class).blockLast();
    }

    private static Validator createValidator() throws IOException {
        final var heroes = JsonDeserializer
                        .deserializeHeroList(org.tbplusc.app.util.HttpGetter
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
}
