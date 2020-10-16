package org.tbplusc.app.discordinteraction;

import discord4j.core.object.entity.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.talenthelper.HeroConsts;
import org.tbplusc.app.talenthelper.parsers.ITalentProvider;
import org.tbplusc.app.talenthelper.parsers.IcyVeinsTalentProvider;
import org.tbplusc.app.validator.WordDistancePair;

import java.io.IOException;
import java.util.List;

import static org.tbplusc.app.discordinteraction.DiscordUtil.getChannelForMessage;

public class HeroSelectionState implements ChatState {
    private static final Logger logger = LoggerFactory.getLogger(HeroSelectionState.class);

    private final List<WordDistancePair> availableHeroes;

    private final Message message;
    private final ITalentProvider talentProvider;

    public HeroSelectionState(List<WordDistancePair> availableHeroes, Message message,
                    ITalentProvider talentProvider) {
        this.availableHeroes = availableHeroes;
        this.message = message;
        this.talentProvider = talentProvider;
        showInitMessage();
    }

    private void showInitMessage() {
        final var channel = getChannelForMessage(message);
        final var heroes = new StringBuilder();
        for (var i = 0; i < availableHeroes.size(); i++) {
            heroes.append(String.format("%3d. %s \n", i + 1, availableHeroes.get(i).word));
        }
        channel.createMessage(String.format("Choice hero (type number): \n ```md\n%s```", heroes))
                        .block();
    }

    public static void showHeroBuildToDiscord(Message message, String heroName,
                    ITalentProvider talentProvider) {
        final var normalizedHeroName = IcyVeinsTalentProvider.normalizeHeroName(heroName);
        logger.info("Normalized hero name: {}", normalizedHeroName);
        try {
            final var builds = talentProvider.getBuilds(normalizedHeroName);
            final var channel = getChannelForMessage(message);
            channel.createMessage(String.format("Selected hero is **%s**", normalizedHeroName))
                            .block();
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
        if (number < 1 || number >= 10) {
            getChannelForMessage(message).createMessage("Wrong number").block();
            return this;
        }
        final var heroName = availableHeroes.get(number - 1).word;
        showHeroBuildToDiscord(message, heroName, talentProvider);
        return new DefaultChatState();
    }
}
