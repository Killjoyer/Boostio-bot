package org.tbplusc.app.discordinteraction;

import discord4j.core.object.entity.Message;

public interface ChatState {
    ChatState handleMessage(Message message);
}
