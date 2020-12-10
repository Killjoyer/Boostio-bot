package org.tbplusc.app.discord.interaction;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import org.slf4j.Logger;
import org.tbplusc.app.message.processing.MessageHandler;
import org.tbplusc.app.util.EnvWrapper;

import java.util.Map;
import java.util.TreeMap;

public class DiscordInitializer {
    public static final String token = EnvWrapper.getValue("DISCORD_TOKEN");
    public static final DiscordClient client = DiscordClient.create(token);
    public static final GatewayDiscordClient gateway = client.login().block();
    public static final Map<String, Integer> namesToNums = new TreeMap<>();
    static {
        namesToNums.put("1\uFE0F\u20E3", 1);
        namesToNums.put("2\uFE0F\u20E3", 2);
        namesToNums.put("3\uFE0F\u20E3", 3);
        namesToNums.put("4\uFE0F\u20E3", 4);
        namesToNums.put("5\uFE0F\u20E3", 5);
        namesToNums.put("6\uFE0F\u20E3", 6);
        namesToNums.put("7\uFE0F\u20E3", 7);
        namesToNums.put("8\uFE0F\u20E3", 8);
        namesToNums.put("9\uFE0F\u20E3", 9);
        namesToNums.put(Character.toString(128287), 10);
    }

    public DiscordInitializer() {
        throw new IllegalStateException("Utility class");
    }

    public static void initialize(MessageHandler messageHandler, Logger logger) {
        if (gateway == null) {
            logger.error("Can't connect to discord");
            return;
        }
        gateway.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage).filter(message -> {
            var authorOptional = message.getAuthor();
            if (authorOptional.isEmpty()) return true;
            return !authorOptional.get().getId().equals(client.getCoreResources().getSelfId());
        })
                        .subscribe(message -> messageHandler
                                        .handleMessage(new WrappedDiscordMessage(message)));
        gateway.on(ReactionAddEvent.class).filter(reactionAddEvent -> {
            var customEmoji = reactionAddEvent.getEmoji().asUnicodeEmoji().orElse(null);
            if (customEmoji == null) return false;
            var reactionAuthorOptional = reactionAddEvent.getMember();
            if (reactionAuthorOptional.isEmpty()) return false;
            if (reactionAuthorOptional.get().getId().equals(client.getCoreResources().getSelfId()))
            logger.info("provided reaction is: " + reactionAddEvent.getEmoji());
            return namesToNums.containsKey(customEmoji.getRaw());
        }).subscribe(reactionEvent -> messageHandler
                        .handleMessage(new WrappedReaction(reactionEvent)));
        gateway.on(DisconnectEvent.class).blockLast();
        logger.info("Discord initialized");
    }
}
