/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: PlayerManager.java
 * Last modified: 04/03/2023, 22:39
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL
 * COPIES OR SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 */

package pl.miloszgilga.audioplayer;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;

import org.springframework.stereotype.Component;
import org.apache.http.client.config.RequestConfig;

import java.util.*;

import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.AudioPlayerException.TrackIsNotPlayingException;
import static pl.miloszgilga.exception.AudioPlayerException.InvokerIsNotTrackSenderOrAdminException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Slf4j
@Component
public class PlayerManager extends DefaultAudioPlayerManager implements IPlayerManager {

    private static final int CONNECTION_TIMEOUT = 10000;

    private final BotConfiguration config;
    private final EmbedMessageBuilder builder;
    private final Map<Long, MusicManager> musicManagers = new HashMap<>();

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    PlayerManager(BotConfiguration config, EmbedMessageBuilder builder) {
        this.config = config;
        this.builder = builder;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void initialize() {
        AudioSourceManagers.registerLocalSource(this);
        AudioSourceManagers.registerRemoteSources(this);
        setHttpRequestConfigurator(config -> RequestConfig.copy(config).setConnectTimeout(CONNECTION_TIMEOUT).build());
        source(YoutubeAudioSourceManager.class)
            .setPlaylistPageCount(config.getProperty(BotProperty.J_PAGINATION_MAX, Integer.class));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void loadAndPlay(CommandEventWrapper event, String trackUrl, boolean isUrlPattern) {
        final MusicManager musicManager = getMusicManager(event);
        final AudioLoadResultHandler audioLoadResultHandler = new AudioLoaderResultImpl(musicManager, config,
            builder, event, isUrlPattern);
        event.getGuild().getAudioManager().setSelfDeafened(true);
        loadItemOrdered(musicManager, trackUrl, audioLoadResultHandler);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void pauseCurrentTrack(CommandEventWrapper event) {
        final MusicManager musicManager = checkPermissions(event);
        musicManager.getAudioPlayer().setPaused(true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void resumeCurrentTrack(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        final AudioTrack pausedTrack = musicManager.getTrackScheduler().getPausedTrack();
        if (invokerIsNotTrackSenderOrAdmin(pausedTrack, event)) {
            throw new InvokerIsNotTrackSenderOrAdminException(config, event);
        }
        musicManager.getAudioPlayer().setPaused(false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public AudioTrackInfo skipCurrentTrack(CommandEventWrapper event) {
        final MusicManager musicManager = checkPermissions(event);
        final AudioTrackInfo skippedTrack = getCurrentPlayingTrack(event);
        musicManager.getTrackScheduler().nextTrack();
        log.info("G: {}, A: {} <> Current playing track '{}' was skipped", event.guildName(), event.authorTag(),
            skippedTrack.title);
        return skippedTrack;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void shuffleQueue(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        Collections.shuffle((List<?>)musicManager.getQueue());
        log.info("G: {}, A: {} <> Current queue tracks was shuffled", event.guildName(), event.authorTag());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void repeatCurrentTrack(CommandEventWrapper event, int countOfRepeats) {
        final MusicManager musicManager = checkPermissions(event);
        musicManager.getTrackScheduler().setCountOfRepeats(countOfRepeats);
        if (countOfRepeats == 0) {
            log.info("G: {}, A: {} <> Repeating of current playing track '{}' was removed", event.guildName(),
                event.authorTag(), getCurrentPlayingTrack(event).title);
            return;
        }
        log.info("G: {}, A: {} <> Current playing track '{}' will be repeating {}x times",
            event.guildName(), event.authorTag(), getCurrentPlayingTrack(event).title, countOfRepeats);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean toggleInfiniteLoopCurrentTrack(CommandEventWrapper event) {
        final MusicManager musicManager = checkPermissions(event);
        musicManager.getTrackScheduler().setInfiniteRepeating(!musicManager.getTrackScheduler().isInfiniteRepeating());
        final boolean isRepeating = musicManager.getTrackScheduler().isInfiniteRepeating();
        if (isRepeating) {
            log.info("G: {}, A: {} <> Current playing track '{}' has been placed in infinite loop",
                event.guildName(), event.authorTag(), getCurrentPlayingTrack(event).title);
        } else {
            log.info("G: {}, A: {} <> Current playing track '{}' has been removed from infinite loop",
                event.guildName(), event.authorTag(), getCurrentPlayingTrack(event).title);
        }
        return musicManager.getTrackScheduler().isInfiniteRepeating();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setPlayerVolume(CommandEventWrapper event, int volume) {
        final MusicManager musicManager = getMusicManager(event);
        musicManager.getAudioPlayer().setVolume(volume);
        log.info("G: {}, A: {} <> Audio player volume was set to '{}' volume units", event.guildName(),
            event.authorTag(), volume);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private MusicManager checkPermissions(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        final AudioTrack playingTrack = musicManager.getAudioPlayer().getPlayingTrack();
        if (Objects.isNull(playingTrack)) {
            throw new TrackIsNotPlayingException(config, event);
        }
        if (invokerIsNotTrackSenderOrAdmin(playingTrack, event)) {
            throw new InvokerIsNotTrackSenderOrAdminException(config, event);
        }
        return musicManager;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private boolean invokerIsNotTrackSenderOrAdmin(AudioTrack track, CommandEventWrapper event) {
        final User dataSender = ((Member) track.getUserData()).getUser();
        final Member messageSender = event.getGuild().getMember(event.getAuthor());
        if (Objects.isNull(messageSender)) return true;

        final ValidateUserDetails details =  Utilities.validateUserDetails(event, config);
        return !(dataSender.getAsTag().equals(event.getAuthor().getAsTag()) || !details.isNotOwner()
            || !details.isNotManager() || !details.isNotDj());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MusicManager getMusicManager(CommandEventWrapper event) {
        return musicManagers.computeIfAbsent(event.getGuild().getIdLong(), guildId -> {
            final MusicManager musicManager = new MusicManager(this, builder, config, event.getGuild(), event);
            event.getGuild().getAudioManager().setSendingHandler(musicManager.getAudioPlayerSendHandler());
            return musicManager;
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MusicManager getMusicManager(Guild guild) {
        return musicManagers.get(guild.getIdLong());
    }

    public ExtendedAudioTrackInfo getCurrentPlayingTrack(CommandEventWrapper event) {
        final MusicManager musicManager = getMusicManager(event);
        if (Objects.isNull(musicManager.getAudioPlayer().getPlayingTrack())) return null;
        return new ExtendedAudioTrackInfo(musicManager.getAudioPlayer().getPlayingTrack());
    }
}
