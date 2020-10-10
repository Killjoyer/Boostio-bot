package org.tbplusc.app.discordinteraction;

import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.talenthelper.HeroConsts;
import org.tbplusc.app.talenthelper.icyveinsparser.IcyVeinsParser;

import java.io.IOException;
import java.util.List;

import static org.tbplusc.app.discordinteraction.DiscordUtil.getChannelForMessage;

public class HeroSelectionState implements ChatState {
    private static final Logger logger = LoggerFactory.getLogger(HeroSelectionState.class);

    private final List<String> availableHeroes;

    private final Message message;

    public HeroSelectionState(List<String> availableHeroes, Message message) {
        this.availableHeroes = availableHeroes;
        this.message = message;
        showInitMessage();
    }

    public void showInitMessage() {
        final var channel = getChannelForMessage(message);
        final var heroes = new StringBuilder();
        for (var i = 0; i < availableHeroes.size(); i++) {
            heroes.append(String.format("%3d. %s \n", i + 1, availableHeroes.get(i)));
        }
        channel.createMessage(String.format("Choice hero (type number): \n ```md\n%s```", heroes))
                        .block();
    }

    private static void showHeroBuildToDiscord(Message message, String heroName) {
        logger.info("Normalized hero name: {}", heroName);
        try {
            final var builds = IcyVeinsParser.getBuildsByHeroName(heroName);
            final var channel = getChannelForMessage(message);
            channel.createMessage(String.format("Selected hero is **%s**", heroName)).block();
            builds.getBuilds().stream().map((build) -> {
                final var talents = new StringBuilder();
                for (var i = 0; i < build.getTalents().size(); i++) {
                    talents.append(String.format("%3d. %s \n", HeroConsts.HERO_TALENTS_LEVELS[i],
                                    build.getTalents().get(i)));
                }
                return String.format("**%s**: ```md\n%s```**Description:** %s", build.getName(),
                                talents, build.getDescription());
            }).forEach(build -> channel.createMessage(build).block());
        } catch (IOException e) {
            logger.error("Hero was not loaded", e);
        }
    }

    @Override public ChatState handleMessage(Message message) {
        var number = Integer.parseInt(message.getContent());
        if (number <= 1 || number >= 10) {
            getChannelForMessage(message).createMessage("Wrong number").block();
            return this;
        }
        final var heroName = IcyVeinsParser.normalizeHeroName(availableHeroes.get(number - 1));
        showHeroBuildToDiscord(message, heroName);
        return new DefaultChatState();
    }
}
