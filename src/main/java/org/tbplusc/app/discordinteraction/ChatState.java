package org.tbplusc.app.discordinteraction;


public interface ChatState {
    ChatState handleMessage(WrappedMessage message);
}
