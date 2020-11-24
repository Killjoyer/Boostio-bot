package org.tbplusc.app.discord.interaction;

import discord4j.core.object.entity.Message;
import org.tbplusc.app.message.processing.MessageSender;
import org.tbplusc.app.message.processing.WrappedMessage;
import static org.tbplusc.app.discord.interaction.DiscordUtil.getChannelForMessage;

public class WrappedDiscordMessage implements WrappedMessage {
    private final Message message;

    @Override
    public MessageSender getSenderApp() {
        return MessageSender.discord;
    }

    public WrappedDiscordMessage(Message message) {
        this.message = message;
    }

    @Override
    public String getConversationId() {
        final var authorOptional = message.getAuthor();
        if (authorOptional.isEmpty()) {
            throw new NullPointerException("Message had no author");
        }
        final var authorId = authorOptional.get().getId();
        final var channel = message.getChannel().block();
        if (channel == null) {
            throw new NullPointerException("No channel for the message");
        }
        final var channelId = channel.getId();
        return authorId.asString() + channelId.asString();
    }

    @Override
    public String getContent() {
        return message.getContent();
    }

    @Override
    public void respond(String text) {
        var channel = getChannelForMessage(message);
        channel.createMessage(text).block();
    }

    @Override
    public String getServerId() {
        var guildId = message.getGuildId();
        return guildId.orElseThrow(() -> new NullPointerException("No server for message"))
                        .asString();
    }
}
