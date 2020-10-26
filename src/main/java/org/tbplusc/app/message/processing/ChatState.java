package org.tbplusc.app.message.processing;


public interface ChatState {
    ChatState handleMessage(WrappedMessage message);
}
