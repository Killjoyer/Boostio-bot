package org.tbplusc.app.discord_interaction;

import discord4j.core.object.entity.Message;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.tbplusc.app.discord_interaction.DiscordUtil.getChannelForMessage;

public class DefaultChatState implements ChatState {
    private final String _prefix;

    private static final Map<String, BiFunction<String, Message, ChatState>> _commands = new HashMap<>();

    public static void registerCommand(String name, BiFunction<String, Message, ChatState> action) {
        _commands.put(name, action);
    }

    public static void registerDefaultCommands() {
        registerCommand("echo", (args, message) -> {
            final var channel = getChannelForMessage(message);
            channel.createMessage(
                    args.equals("") ? "Не могу заэхоть пустую строку" : args
            ).block();
            return new DefaultChatState();
        });
        registerCommand("authors", (args, message) -> {
            final var channel = getChannelForMessage(message);
            channel.createMessage("Код писали: Александ Жмышенко, Олег Белахахлий и Semen Зайдельман").block();
            return new DefaultChatState();
        });
    }

    public DefaultChatState() {
        _prefix = System.getenv("DISCORD_PREFIX");
    }

    @Override
    public ChatState handleMessage(Message message) {
        final var content = message.getContent();
        if (!content.startsWith(_prefix)) return this;
        final var splitted = content.split(" ", 2);
        final var command = splitted[0].substring(1);
        if (!_commands.containsKey(command)) return this;
        final var args = splitted.length > 1 ? splitted[1] : "";
        return _commands.get(command).apply(args, message);
    }
}
