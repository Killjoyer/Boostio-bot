package org.tbplusc.app.discordinteraction;

import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.talenthelper.HeroConsts;
import org.tbplusc.app.talenthelper.icyveinsparser.IcyVeinsParser;
import org.tbplusc.app.validator.Validator;

import static org.tbplusc.app.discordinteraction.DiscordUtil.getChannelForMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DefaultChatState implements ChatState {
    private static final Logger logger = LoggerFactory.getLogger(DefaultChatState.class);

    private final String prefix;

    private static final Map<String, BiFunction<String, Message, ChatState>> commands =
                    new HashMap<>();

    public static void registerCommand(String name, BiFunction<String, Message, ChatState> action) {
        commands.put(name, action);
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
            channel.createMessage(
                            "Код писали: Александ Жмышенко, Олег Белахахлий и Semen Зайдельман")
                            .block();
            return new DefaultChatState();
        });
        registerCommand("build", (args, message) -> {
            final var validator = new Validator();
            logger.info("Typed hero name: {}", args);
            final var heroName = IcyVeinsParser
                            .normalizeHeroName(validator.getClosestToInput(args));
            logger.info("Normalized hero name: {}", heroName);
            try {
                final var builds = IcyVeinsParser.getBuildsByHeroName(heroName);
                final var channel = getChannelForMessage(message);
                channel.createMessage(
                                String.format("Selected hero is **%s**", heroName)
                ).block();
                builds.getBuilds().stream().map((build) -> {
                    final var talents = new StringBuilder();
                    for (var i = 0; i < build.getTalents().size(); i++) {
                        talents.append(String.format(
                                        "%3d. %s \n",
                                        HeroConsts.HERO_TALENTS_LEVELS[i],
                                        build.getTalents().get(i))
                        );
                    }
                    return String.format("**%s**: ```md\n%s```**Description:** %s",
                                    build.getName(), talents, build.getDescription());
                })
                                .forEach(build -> channel.createMessage(build).block());
            } catch (IOException e) {
                logger.error("Hero was not loaded", e);
            }
            return new DefaultChatState();
        });
    }

    public DefaultChatState() {
        prefix = System.getenv("DISCORD_PREFIX");
    }

    @Override
    public ChatState handleMessage(Message message) {
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
