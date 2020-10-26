package org.tbplusc.app.discord.interaction;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MessageHandler {
    private final Map<String, UserStore> states = new ConcurrentHashMap<>();
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
            states.put(key, new UserStore());
        }
        final var userStore = states.get(key);
        final var unlocked = userStore.messageInProcess.tryLock();
        if (!unlocked) {
            message.respond("Your previous message in process");
            return;
        }
        userStore.setState(userStore.getState().handleMessage(message));
        userStore.messageInProcess.unlock();
    }
}
