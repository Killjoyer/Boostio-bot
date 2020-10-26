package org.tbplusc.app.discord.interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;
import org.tbplusc.app.util.EnvWrapper;
import org.tbplusc.app.validator.Validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


/**
 * ChatState that responsible for handling commands on first step
 */
public class DefaultChatState implements ChatState {
    private static final Logger logger = LoggerFactory.getLogger(DefaultChatState.class);

    private final String prefix;

    private static final Map<String, BiFunction<String, WrappedMessage, ChatState>> commands =
                    new HashMap<>();

    public static void registerCommand(String name, BiFunction<String, WrappedMessage, ChatState> action) {
        commands.put(name, action);
    }

    /**
     * Register "echo", "authors", "builds" commands.
     * @param validator object to find closest word for "builds" command
     * @param talentProvider object to get builds for hero
     */
    public static void registerDefaultCommands(Validator validator,
                    ITalentProvider talentProvider) {
        registerCommand("echo", (args, message) -> {
            message.respond(args.equals("") ? "Не могу заэхоть пустую строку" : args);
            return new DefaultChatState();
        });
        registerCommand("authors", (args, message) -> {
            message.respond("Код писали: Александ Жмышенко, Олег Белахахлий и Semen Зайдельман");
            return new DefaultChatState();
        });
        registerCommand("build", (args, message) -> {
            logger.info("Typed hero name: {}", args);
            final var possibleHeroNames = validator.getSomeClosestToInput(args, 10);
            if (possibleHeroNames[0].distance == 0) {
                HeroSelectionState.showHeroBuildToDiscord(message, possibleHeroNames[0].word,
                                talentProvider);
                return new DefaultChatState();
            }
            return new HeroSelectionState(Arrays.asList(possibleHeroNames.clone()), message,
                            talentProvider);
        });
    }

    public DefaultChatState() {
        prefix = EnvWrapper.getValue("DISCORD_PREFIX");
    }

    @Override public ChatState handleMessage(WrappedMessage message) {
        final var content = message.getContent();
        if (!content.startsWith(prefix)) {
            return this;
        }
        final var splitted = content.split(" ", 2);
        final var command = splitted[0].substring(1);
        if (!commands.containsKey(command)) {
            return this;
        }
        final var args = splitted.length > 1 ? splitted[1] : "";
        return commands.get(command).apply(args, message);
    }
}
