package org.tbplusc.app.message.processing;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class UserStore {
    private ChatState state;

    public final Lock messageInProcess = new ReentrantLock();

    public UserStore() {
        state = new DefaultChatState();
    }

    public ChatState getState() {
        return state;
    }

    public void setState(ChatState state) {
        this.state = state;
    }
}
