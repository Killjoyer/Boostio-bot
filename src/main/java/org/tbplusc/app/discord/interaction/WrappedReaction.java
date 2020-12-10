package org.tbplusc.app.discord.interaction;

import discord4j.core.event.domain.message.ReactionAddEvent;
import org.tbplusc.app.message.processing.MessageSender;
import org.tbplusc.app.message.processing.WrappedBotRespondMessage;
import org.tbplusc.app.message.processing.WrappedMessage;

public class WrappedReaction implements WrappedMessage {
    private final ReactionAddEvent reaction;

    public WrappedReaction(ReactionAddEvent reaction) {
        this.reaction = reaction;
    }

    @Override public MessageSender getSenderApp() {
        return MessageSender.discord;
    }

    @Override public String getConversationId() {
        final var authorOptional = reaction.getUser().block();
        if (authorOptional == null) {
            throw new NullPointerException("Can't get reaction author");
        }
        final var authorId = authorOptional.getId();
        final var channel = reaction.getChannel().block();
        if (channel == null) {
            throw new NullPointerException("No channel for the message");
        }
        final var channelId = channel.getId();
        return authorId.asString() + channelId.asString();
    }

    @Override public String getServerId() {
        var guildId = reaction.getGuildId();
        return guildId.orElseThrow(() -> new NullPointerException("No server for message"))
                        .asString();
    }

    @Override public String getContent() {
        return DiscordInitializer.namesToNums.get(reaction.getEmoji().asUnicodeEmoji().orElseThrow().getRaw()).toString();
    }

    @Override public WrappedBotRespondMessage respond(String text, boolean keyboarded) {
        var channel = reaction.getChannel().block();
        if (channel == null) {
            throw new NullPointerException("Can't get channel for reaction");
        }
        return new WrappedDiscordBotRespondMessage(channel.createMessage(text).block());
    }
}
