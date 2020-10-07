package org.tbplusc.app.discordinteraction;

import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.MessageChannel;

public class DiscordUtil {
    public static MessageChannel getChannelForMessage(Message message) {
        final var channel = message.getChannel().block();
        if (channel == null) {
            throw new NullPointerException("Channel was null");
        }
        return channel;
    }
}
