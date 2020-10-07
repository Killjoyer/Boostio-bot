package org.tbplusc.app.discord_interaction;

import discord4j.core.object.entity.Message;

public interface ChatState {
    ChatState handleMessage(Message message);
}
