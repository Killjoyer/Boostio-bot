package org.tbplusc.app.message.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MessageHandler {
    private final Map<String, UserStore> states = new ConcurrentHashMap<>();
    private final ExecutorService threadPool;
    private static final Logger logger = LoggerFactory.getLogger(MessageHandler.class);

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
        if (!userStore.messageInProcess.tryLock()) {
            message.respond("Your previous message in process");
            return;
        }
        try {
            logger.info("Started processing message from {}", key);
            userStore.setState(userStore.getState().handleMessage(message));
        } catch (Exception e) {
            logger.error("Message processing failed with error", e);
            return;
        } finally {
            userStore.messageInProcess.unlock();
        }
        logger.info("Finished processing message from {}", key);
    }
}
