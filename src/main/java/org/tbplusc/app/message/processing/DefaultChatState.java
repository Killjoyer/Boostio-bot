package org.tbplusc.app.message.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.db.IAliasesDBInteractor;
import org.tbplusc.app.db.IPrefixDBInteractor;
import org.tbplusc.app.discord.interaction.WrappedDiscordMessage;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;
import org.tbplusc.app.util.EnvWrapper;
import org.tbplusc.app.validator.Validator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


/**
 * ChatState that responsible for handling commands on first step.
 */
public class DefaultChatState implements ChatState {
    private static final Logger logger = LoggerFactory.getLogger(DefaultChatState.class);

    private final String defaultPrefix;

    private static final Map<String, BiFunction<String, WrappedMessage, ChatState>> commands =
                    new HashMap<>();

    public static void registerCommand(String name,
                    BiFunction<String, WrappedMessage, ChatState> action) {
        commands.put(name, action);
    }

    /**
     * Register "echo", "authors", "builds" commands.
     * 
     * @param validator           object to find closest word for "builds" command
     * @param talentProvider      object to get builds for hero
     * @param aliasesDBInteractor object working with aliases
     * @param prefixDBInteractor  object working with discord related settings
     */
    public static void registerDefaultCommands(Validator validator, ITalentProvider talentProvider,
                    IAliasesDBInteractor aliasesDBInteractor,
                    IPrefixDBInteractor prefixDBInteractor) {
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
        registerCommand("prefix", (args, message) -> {
            final var guildId = ((WrappedDiscordMessage) message).getServerId(); // FIXME
            logger.info("Server id: {}", guildId);
            prefixDBInteractor.setPrefix(guildId, args);
            return new DefaultChatState();
        });
        registerCommand("alias", (args, message) -> {
            final var guildId = ((WrappedDiscordMessage) message).getServerId();
            logger.info("Server id: {}", guildId);
            final var argsSplit = args.split(" ", 2);
            aliasesDBInteractor.addAlias(guildId, argsSplit[0], argsSplit[1]);
            return new DefaultChatState();
        });
        registerCommand("rmv-alias", (args, message) -> {
            final var guildId = ((WrappedDiscordMessage) message).getServerId();
            logger.info("Server id: {}", guildId);
            aliasesDBInteractor.removeAlias(guildId, args);
            return new DefaultChatState();
        });
    }

    public DefaultChatState() {
        defaultPrefix = EnvWrapper.getValue("DISCORD_PREFIX");
    }

    @Override
    public ChatState handleMessage(WrappedMessage message) {
        final var content = message.getContent();
        logger.info("Message content: {}", content);
        if (message.getSender().isPrefixed()
                        && (!content.startsWith(defaultPrefix) || content.equals(""))) {
            return this;
        }
        final var splitted = content.split(" ", 2);
        final var command =
                        message.getSender().isPrefixed() ? splitted[0].substring(1) : splitted[0];
        if (!commands.containsKey(command)) {
            return this;
        }
        final var args = splitted.length > 1 ? splitted[1] : "";
        return commands.get(command).apply(args, message);
    }
}
