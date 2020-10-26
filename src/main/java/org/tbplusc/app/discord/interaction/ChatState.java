package org.tbplusc.app.discord.interaction;


public interface ChatState {
    ChatState handleMessage(WrappedMessage message);
}
