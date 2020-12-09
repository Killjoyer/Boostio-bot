package org.tbplusc.app.discord.interaction;

import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;
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
    public WrappedDiscordBotRespondMessage respond(String text, boolean keyboarded) {
        var channel = getChannelForMessage(message);
        var resultMessage = channel.createMessage(text).block();
        if (keyboarded && resultMessage != null) {
            for (var reaction : DiscordInitializer.namesToNums.keySet()) {
                resultMessage.addReaction(ReactionEmoji.unicode(reaction)).block();
            }
        }
        return new WrappedDiscordBotRespondMessage(resultMessage);
    }

    @Override
    public WrappedDiscordBotRespondMessage respond(String text) {
        return respond(text, false);
    }

    @Override
    public String getServerId() {
        var guildId = message.getGuildId();
        return guildId.orElseThrow(() -> new NullPointerException("No server for message"))
                        .asString();
    }
}
