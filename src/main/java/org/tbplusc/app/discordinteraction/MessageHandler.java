package org.tbplusc.app.discordinteraction;

import discord4j.core.object.entity.Message;

import java.util.HashMap;
import java.util.Map;

public class MessageHandler {
    final private Map<String, ChatState> _states = new HashMap<>();

    public void HandleMessage(Message message) {
        final var thread = new Thread(() -> {
            ProcessMessage(message);
        });
        thread.start();
    }

    private void ProcessMessage(Message message) {
        final var authorOptional = message.getAuthor();
        if (authorOptional.isEmpty()) throw new NullPointerException("Message had no author");
        final var authorId = authorOptional.get().getId();
        final var channel = message.getChannel().block();
        if (channel == null) throw new NullPointerException("No channel for the message");
        final var channelId = channel.getId();
        final var key = authorId.asString() + channelId.asString();
        if (!_states.containsKey(key)) _states.put(key, new DefaultChatState());
        _states.put(key, _states.get(key).handleMessage(message));
    }
}
