package org.tbplusc.app;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.tbplusc.app.discordinteraction.DefaultChatState;
import org.tbplusc.app.discordinteraction.MessageHandler;
import reactor.core.publisher.Mono;

public class Main {
    public static void main(String[] args) {
        final var token = System.getenv("DISCORD_TOKEN");
        final var client = DiscordClient.create(token);
        DefaultChatState.registerDefaultCommands();
        final var messageHandler = new MessageHandler();
        client.login()
                .flatMapMany(gateway -> gateway.on(MessageCreateEvent.class))
                .map(MessageCreateEvent::getMessage)
                .map(message -> {
                    messageHandler.HandleMessage(message);
                    return Mono.empty();
                }).blockLast();
    }
}
