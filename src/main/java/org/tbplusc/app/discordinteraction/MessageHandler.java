package org.tbplusc.app.discordinteraction;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MessageHandler {
    private final Map<String, ChatState> states = new HashMap<>();
    private final ExecutorService threadPool;

    public MessageHandler() {
        this.threadPool = Executors.newFixedThreadPool(24);
    }

    public Future<?> handleMessage(WrappedMessage message) {
        return threadPool.submit(() -> processMessage(message));
    }

    private void processMessage(WrappedMessage message) {
        final var key = message.getContextKey();
        if (!states.containsKey(key)) {
            states.put(key, new DefaultChatState());
        }
        states.put(key, states.get(key).handleMessage(message));
    }
}
