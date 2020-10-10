package org.tbplusc.app;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.discordinteraction.DefaultChatState;
import org.tbplusc.app.discordinteraction.MessageHandler;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Application started");
        final var token = System.getenv("DISCORD_TOKEN");
        final var client = DiscordClient.create(token);
        DefaultChatState.registerDefaultCommands();
        final var messageHandler = new MessageHandler();
        final var gateway = client.login().block();
        if (gateway == null) {
            logger.error("Can't connect to discord");
            return;
        }
        gateway.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
                        .subscribe(messageHandler::handleMessage);
        gateway.on(DisconnectEvent.class).blockLast();
    }
}
