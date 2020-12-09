package org.tbplusc.app.message.processing;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.db.FailedReadException;
import org.tbplusc.app.db.FailedWriteException;
import org.tbplusc.app.db.IAliasesDBInteractor;
import org.tbplusc.app.db.IBuildDBCacher;
import org.tbplusc.app.db.IPrefixDBInteractor;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;
import org.tbplusc.app.validator.Validator;


/**
 * ChatState that responsible for handling commands on first step.
 */
public class DefaultChatState implements ChatState {
    private static final Logger logger = LoggerFactory.getLogger(DefaultChatState.class);

    private static final Map<String, BiFunction<String, WrappedMessage, ChatState>> commands =
                    new HashMap<>();

    private final IAliasesDBInteractor aliasesDBInteractor;
    private final IPrefixDBInteractor prefixDBInteractor;


    public static void registerCommand(String name,
                    BiFunction<String, WrappedMessage, ChatState> action) {
        commands.put(name, action);
    }

    public DefaultChatState(IAliasesDBInteractor aliasesDBInteractor,
                    IPrefixDBInteractor prefixDBInteractor) {
        this.aliasesDBInteractor = aliasesDBInteractor;
        this.prefixDBInteractor = prefixDBInteractor;
    }

    /**
     * Register "echo", "authors", "builds" commands.
     *
     * @param validator           object to find closest word for "builds" command
     * @param talentProvider      object to get builds for hero
     * @param aliasesDBInteractor object working with aliases
     * @param prefixDBInteractor  object working with discord related settings
     */
    public static void registerDefaultCommands(DefaultChatState defaultChatState,
                    Validator validator, ITalentProvider talentProvider,
                    IAliasesDBInteractor aliasesDBInteractor,
                    IPrefixDBInteractor prefixDBInteractor, IBuildDBCacher buildDBCacher) {
        registerCommand("echo", (args, message) -> {
            message.respond(args.equals("") ? "Не могу заэхоть пустую строку" : args);
            return defaultChatState;
        });
        registerCommand("authors", (args, message) -> {
            message.respond("Код писали: Александ Жмышенко, Олег Белахахлий и Semen Зайдельман");
            return defaultChatState;
        });
        registerCommand("build", (args, message) -> {
            try {
                logger.info("Typed hero name: {}", args);
                var aliases = aliasesDBInteractor.getAliases(message.getServerId());
                final var possibleHeroNames = validator.getSomeClosestToInput(args, 10, aliases);
                if (possibleHeroNames[0].distance == 0) {
                    HeroSelectionState.showHeroBuildToDiscord(message, possibleHeroNames[0].hero,
                                    talentProvider, buildDBCacher);
                    return defaultChatState;
                }
                return new HeroSelectionState(Arrays.asList(possibleHeroNames.clone()), message,
                                talentProvider, buildDBCacher);
            } catch (FailedReadException e) {
                message.respond("Unable to get hero from DB");
                return defaultChatState;
            }
        });
        registerCommand("prefix", (args, message) -> {
            if (message.getSenderApp() == MessageSender.discord) {
                final var serverId = message.getServerId();
                logger.info("Server id: {}", serverId);
                try {
                    prefixDBInteractor.setPrefix(serverId, args);
                } catch (FailedWriteException e) {
                    logger.error("Unable to write prefix to DB", e);
                }
            } else {
                message.respond("Нельзя изменить префикс");
            }
            return defaultChatState;
        });
        registerCommand("alias", (args, message) -> {
            final var serverId = message.getServerId();
            logger.info("Server id: {}", serverId);
            final var argsSplit = args.split(" ", 2);
            try {
                aliasesDBInteractor.addAlias(serverId, argsSplit[0], argsSplit[1]);
            } catch (FailedWriteException e) {
                message.respond("Unable to write alias to DB");
            }
            return defaultChatState;
        });
        registerCommand("rmv-alias", (args, message) -> {
            final var serverId = message.getServerId();
            logger.info("Server id: {}", serverId);
            try {
                aliasesDBInteractor.removeAlias(serverId, args);
            } catch (FailedWriteException e) {
                message.respond("Unable to delete alias from DB");
            }
            return defaultChatState;
        });
        registerCommand("clear-cache", (args, message) -> {
            try {
                buildDBCacher.clearCache();
                message.respond("Cache was successfully cleared");
            } catch (FailedWriteException e) {
                message.respond("Unable to clear DB cache");
            }
            return defaultChatState;
        });
    }

    @Override
    public ChatState handleMessage(WrappedMessage message) {
        var prefix = message.getSenderApp().prefix;
        if (message.getSenderApp() == MessageSender.discord) {
            try {
                prefix = prefixDBInteractor.getPrefix(message.getServerId());
            } catch (FailedReadException e) {
                try {
                    prefixDBInteractor.setPrefix(message.getServerId(), prefix);
                } catch (FailedWriteException b) {
                    message.respond("Билли бонс умер");
                    return this;
                }
            }
        }
        final var content = message.getContent();
        logger.info("Message content: {}", content);
        logger.info("User's prefix is: {}", prefix);
        if (message.getSenderApp().hasPrefix()
                        && (!content.startsWith(prefix) || content.equals(""))) {
            return this;
        }
        final var splitted = content.split(" ", 2);
        final var command =
                        message.getSenderApp().hasPrefix() ? splitted[0].substring(prefix.length())
                                        : splitted[0];
        if (!commands.containsKey(command)) {
            return this;
        }
        final var args = splitted.length > 1 ? splitted[1] : "";
        return commands.get(command).apply(args, message);
    }
}
