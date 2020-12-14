package org.tbplusc.app.discord.interaction;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import org.tbplusc.app.message.processing.MessageSender;
import org.tbplusc.app.message.processing.WrappedMessage;

import java.util.Arrays;

import static org.tbplusc.app.discord.interaction.DiscordUtil.getChannelForMessage;

public class WrappedDiscordMessage implements WrappedMessage {
    private final Message message;

    public VoiceConnection joinChannelAndPlay() {
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration().setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
        final AudioPlayer player = playerManager.createPlayer();
        VoiceConnection connection = null;
        AudioProvider provider = new LavaPlayerAudioProvider(player);
        var member = message.getAuthorAsMember().block();
        if (member != null) {
            final VoiceState voiceState = member.getVoiceState().block();
            if (voiceState != null) {
                final VoiceChannel channel = voiceState.getChannel().block();
                if (channel != null) {
                    // join returns a VoiceConnection which would be required if we were
                    // adding disconnection features, but for now we are just ignoring it.
                    connection = channel.join(spec -> spec.setProvider(provider)).block();
                }
            }
        }
        var scheduler = new TrackScheduler(player);
        var command = Arrays.asList(message.getContent().split(" "));
        playerManager.loadItem(command.get(1), scheduler);
        return connection;
    }

    @Override
    public MessageSender getSenderApp() {
        return MessageSender.discord;
    }

    public WrappedDiscordMessage(Message message) {
        this.message = message;
    }

    @Override
    public String getConversationId() {
        final var authorOptional = message.getAuthor();
        if (authorOptional.isEmpty()) {
            throw new NullPointerException("Message had no author");
        }
        final var authorId = authorOptional.get().getId();
        final var channel = message.getChannel().block();
        if (channel == null) {
            throw new NullPointerException("No channel for the message");
        }
        final var channelId = channel.getId();
        return authorId.asString() + channelId.asString();
    }

    @Override
    public String getContent() {
        return message.getContent();
    }

    @Override
    public WrappedDiscordBotRespondMessage respond(String text, boolean keyboarded) {
        var channel = getChannelForMessage(message);
        var resultMessage = channel.createMessage(text).block();
        if (keyboarded && resultMessage != null) {
            for (var reaction : DiscordInitializer.namesToNums.keySet()) {
                resultMessage.addReaction(ReactionEmoji.unicode(reaction)).block();
            }
        }
        return new WrappedDiscordBotRespondMessage(resultMessage);
    }

    @Override
    public WrappedDiscordBotRespondMessage respond(String text) {
        return respond(text, false);
    }

    @Override
    public String getServerId() {
        var guildId = message.getGuildId();
        return guildId.orElseThrow(() -> new NullPointerException("No server for message"))
                .asString();
    }
}
