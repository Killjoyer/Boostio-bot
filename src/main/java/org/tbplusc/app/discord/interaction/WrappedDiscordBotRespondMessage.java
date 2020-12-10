package org.tbplusc.app.discord.interaction;

import discord4j.core.object.entity.Message;
import org.tbplusc.app.message.processing.WrappedBotRespondMessage;

public class WrappedDiscordBotRespondMessage implements WrappedBotRespondMessage {
    private final Message message;

    public WrappedDiscordBotRespondMessage(Message message) {
        this.message = message;
    }

    @Override
    public void delete() {
        message.delete().block();
    }
}
