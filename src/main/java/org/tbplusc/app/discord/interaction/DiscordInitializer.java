package org.tbplusc.app.discord.interaction;

import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.slf4j.Logger;
import org.tbplusc.app.message.processing.MessageHandler;
import org.tbplusc.app.util.EnvWrapper;

public class DiscordInitializer {
    public final String token = EnvWrapper.getValue("DISCORD_TOKEN");
    public final DiscordClient client = DiscordClient.create(token);
    public final GatewayDiscordClient gateway = client.login().block();

    public DiscordInitializer(MessageHandler messageHandler, Logger logger) {
        if (gateway == null) {
            logger.error("Can't connect to discord");
            return;
        }
        gateway.on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
                .subscribe(message -> messageHandler
                        .handleMessage(new WrappedDiscordMessage(message)));
        gateway.on(DisconnectEvent.class).blockLast();
    }
}
