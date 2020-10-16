package org.tbplusc.app.discordinteraction;

import discord4j.core.object.entity.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageHandler {
    private final Map<String, ChatState> states = new HashMap<>();
    private final ExecutorService threadPool;

    public MessageHandler() {
        this.threadPool = Executors.newFixedThreadPool(24);
    }

    public void handleMessage(Message message) {
        threadPool.execute(() -> processMessage(message));
    }

    private void processMessage(Message message) {
        final var authorOptional = message.getAuthor();
        if (authorOptional.isEmpty()) {
            throw new NullPointerException("Message had no author");
        }
        final var authorId = authorOptional.get().getId();
        final var channel = message.getChannel().block();
        if (channel == null) {
            throw new NullPointerException("No channel for the message");
        }
        final var channelId = channel.getId();
        final var key = authorId.asString() + channelId.asString();
        if (!states.containsKey(key)) {
            states.put(key, new DefaultChatState());
        }
        states.put(key, states.get(key).handleMessage(message));
    }
}
