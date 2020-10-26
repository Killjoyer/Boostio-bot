package org.tbplusc.app.discord.interaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.talent.helper.HeroConsts;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;
import org.tbplusc.app.talent.helper.parsers.IcyVeinsTalentProvider;
import org.tbplusc.app.validator.WordDistancePair;

import java.io.IOException;
import java.util.List;

/**
 * ChatState to allow user to select hero from closest to his text.
 */
public class HeroSelectionState implements ChatState {
    private static final Logger logger = LoggerFactory.getLogger(HeroSelectionState.class);

    private final List<WordDistancePair> availableHeroes;

    private final WrappedMessage message;
    private final ITalentProvider talentProvider;

    /**
     * ChatState to allow user to select hero from closest to his text.
     * @param availableHeroes heroes list provided by Validator
     * @param message message with command from user
     * @param talentProvider object to get talents from somewhere
     */
    public HeroSelectionState(List<WordDistancePair> availableHeroes, WrappedMessage message,
                    ITalentProvider talentProvider) {
        this.availableHeroes = availableHeroes;
        this.message = message;
        this.talentProvider = talentProvider;
        showInitMessage();
    }

    private void showInitMessage() {
        final var heroes = new StringBuilder();
        for (var i = 0; i < availableHeroes.size(); i++) {
            heroes.append(String.format("%3d. %s \n", i + 1, availableHeroes.get(i).word));
        }
        message.respond(String.format("Choose hero (type number): \n ```md\n%s```", heroes));
    }

    /**
     * Format selected hero build for discord and send it as message response.
     * @param message message to respond
     * @param heroName
     * @param talentProvider object to get talents from somewhere
     */
    public static void showHeroBuildToDiscord(WrappedMessage message, String heroName,
                    ITalentProvider talentProvider) {
        final var normalizedHeroName = IcyVeinsTalentProvider.normalizeHeroName(heroName);
        logger.info("Normalized hero name: {}", normalizedHeroName);
        try {
            final var builds = talentProvider.getBuilds(normalizedHeroName);
            message.respond(String.format("Selected hero is **%s**", normalizedHeroName));
            builds.getBuilds().stream().map((build) -> {
                final var talents = new StringBuilder();
                for (var i = 0; i < build.getTalents().size(); i++) {
                    talents.append(String.format("%3d. %s \n", HeroConsts.HERO_TALENTS_LEVELS[i],
                                    build.getTalents().get(i)));
                }
                return String.format("**%s**: ```md\n%s```**Description:** %s", build.getName(),
                                talents, build.getDescription());
            }).forEach(message::respond);
        } catch (IOException e) {
            logger.error("Hero was not loaded", e);
        }
    }

    @Override public ChatState handleMessage(WrappedMessage message) {
        var number = Integer.parseInt(message.getContent());
        if (number < 1 || number >= 10) {
            message.respond("Wrong number");
            return this;
        }
        final var heroName = availableHeroes.get(number - 1).word;
        showHeroBuildToDiscord(message, heroName, talentProvider);
        return new DefaultChatState();
    }
}
