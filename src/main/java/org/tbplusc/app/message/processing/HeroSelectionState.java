package org.tbplusc.app.message.processing;

import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tbplusc.app.db.FailedReadException;
import org.tbplusc.app.db.FailedWriteException;
import org.tbplusc.app.db.IBuildDBCacher;
import org.tbplusc.app.talent.helper.HeroBuilds;
import org.tbplusc.app.talent.helper.HeroConsts;
import org.tbplusc.app.talent.helper.parsers.ITalentProvider;
import org.tbplusc.app.talent.helper.parsers.IcyVeinsTalentProvider;
import org.tbplusc.app.validator.WordDistancePair;

/**
 * ChatState to allow user to select hero from closest to his text.
 */
public class HeroSelectionState implements ChatState {
    private static final Logger logger = LoggerFactory.getLogger(HeroSelectionState.class);

    private final List<WordDistancePair> availableHeroes;

    private final WrappedMessage message;
    private WrappedBotRespondMessage heroSelectionMessage;
    private final ITalentProvider talentProvider;

    private final IBuildDBCacher buildDBCacher;

    /**
     * ChatState to allow user to select hero from closest to his text.
     *
     * @param availableHeroes heroes list provided by Validator
     * @param message         message with command from user
     * @param talentProvider  object to get talents from somewhere
     */
    public HeroSelectionState(List<WordDistancePair> availableHeroes, WrappedMessage message,
                    ITalentProvider talentProvider, IBuildDBCacher buildDBCacher) {
        this.availableHeroes = availableHeroes;
        this.message = message;
        this.talentProvider = talentProvider;
        this.buildDBCacher = buildDBCacher;
        showInitMessage();
    }

    private void showInitMessage() {
        final var heroes = new StringBuilder();
        for (var i = 0; i < availableHeroes.size(); i++) {
            heroes.append(String.format("%3d. %s \n", i + 1,
                            availableHeroes.get(i).alias.toUpperCase()));
        }
        heroSelectionMessage = message.respond(
                        String.format("Choose hero (type number): \n ```md\n%s```", heroes), true);
    }

    /**
     * Format selected hero build for discord and send it as message response.
     *
     * @param message        message to respond
     * @param heroName       requested hero
     * @param talentProvider object to get talents from somewhere
     */
    public static void showHeroBuildInMarkdown(WrappedMessage message, String heroName,
                    ITalentProvider talentProvider, IBuildDBCacher buildDBCacher) {
        final var normalizedHeroName = IcyVeinsTalentProvider.normalizeHeroName(heroName);
        logger.info("Normalized hero name: {}", normalizedHeroName);
        try {
            HeroBuilds builds;
            try {
                builds = buildDBCacher.getBuilds(normalizedHeroName);
            } catch (FailedReadException e) {
                builds = talentProvider.getBuilds(normalizedHeroName);
                try {
                    buildDBCacher.cacheBuilds(normalizedHeroName, builds);
                } catch (FailedWriteException ex) {
                    logger.error("Failed to cache build for hero {}", normalizedHeroName);
                }
            }
            message.respond(String.format("Selected hero is **%s**",
                            normalizedHeroName.toUpperCase()));
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

    @Override
    public ChatState handleMessage(WrappedMessage message) {
        int number;
        try {
            number = Integer.parseInt(message.getContent());
        } catch (NumberFormatException e) {
            message.respond("Wrong number");
            return this;
        }
        if (number < 1 || number > 10) {
            message.respond("Wrong number");
            return this;
        }
        final var heroName = availableHeroes.get(number - 1).hero;
        if (heroSelectionMessage != null) {
            heroSelectionMessage.delete();
        }
        showHeroBuildInMarkdown(message, heroName, talentProvider, buildDBCacher);
        return null;
    }
}
