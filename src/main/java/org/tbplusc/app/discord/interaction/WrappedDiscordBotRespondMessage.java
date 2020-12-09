package org.tbplusc.app.discord.interaction;

import discord4j.core.object.entity.Message;
import org.tbplusc.app.message.processing.WrappedBotRespondMessage;

public class WrappedDiscordBotRespondMessage implements WrappedBotRespondMessage {
    private final Message message;
    private final WrappedDiscordMessage prevMessage;

    public WrappedDiscordBotRespondMessage(WrappedDiscordMessage prevMessage, Message message) {
        this.message = message;
        this.prevMessage = prevMessage;
    }

    @Override
    public void delete() {
        message.delete().block();
    }
}
