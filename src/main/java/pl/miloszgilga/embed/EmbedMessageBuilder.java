/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: EmbedMessageBuilder.java
 * Last modified: 05/03/2023, 23:38
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

package pl.miloszgilga.embed;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import pl.miloszgilga.dto.*;
import pl.miloszgilga.exception.BugTracker;
import pl.miloszgilga.exception.BotException;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotConfiguration;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@Component
public class EmbedMessageBuilder {

    private final BotConfiguration config;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public EmbedMessageBuilder(BotConfiguration config) {
        this.config = config;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createErrorMessage(CommandEventWrapper wrapper, String message, BugTracker bugTracker) {
        final String tracker = "`" + parseBugTracker(bugTracker) + "`";
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setTitle(config.getLocaleText(LocaleSet.ERROR_HEADER))
            .setDescription(message)
            .appendDescription("\n\n" + config.getLocaleText(LocaleSet.BUG_TRACKER_MESS) + ": " + tracker)
            .setColor(EmbedColor.PURPLE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createErrorMessage(CommandEventWrapper wrapper, BotException ex) {
        return createErrorMessage(wrapper, ex.getMessage(), ex.getBugTracker());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createSingleTrackMessage(CommandEventWrapper wrapper, TrackEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(LocaleSet.ADD_NEW_TRACK_MESS))
            .addField(config.getLocaleText(LocaleSet.TRACK_NAME_MESS) + ":", c.trackUrl(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.TRACK_DURATION_TIME_MESS) + ":", c.durationTime(), true)
            .addField(config.getLocaleText(LocaleSet.TRACK_POSITION_IN_QUEUE_MESS) + ":", c.trackPosition(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.TRACK_ADDDED_BY_MESS) + ":", wrapper.getAuthorTag(), true)
            .setThumbnail(c.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createPlaylistTracksMessage(CommandEventWrapper wrapper, PlaylistEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(LocaleSet.ADD_NEW_PLAYLIST_MESS))
            .addField(config.getLocaleText(LocaleSet.COUNT_OF_TRACKS_MESS) + ":", c.queueTracksCount(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.TRACKS_TOTAL_DURATION_TIME_MESS) + ":", c.queueDurationTime(), true)
            .addField(config.getLocaleText(LocaleSet.TRACK_ADDDED_BY_MESS) + ":", wrapper.getAuthorTag(), true)
            .setThumbnail(c.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createPauseTrackMessage(PauseTrackEmbedContent c) {
        return new EmbedBuilder()
            .setDescription(config.getLocaleText(c.localeSet(), c.localeVariables()))
            .addField(StringUtils.EMPTY, c.pausedVisualizationTrack(), false)
            .addField(config.getLocaleText(LocaleSet.PAUSED_TRACK_TIME_MESS) + ":", c.pausedTimestamp(), true)
            .addField(config.getLocaleText(LocaleSet.PAUSED_TRACK_ESTIMATE_TIME_MESS) + ":", c.estimatedDuration(), true)
            .addField(config.getLocaleText(LocaleSet.PAUSED_TRACK_TOTAL_DURATION_MESS) + ":", c.totalDuration(), true)
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createCurrentPlayingMessage(CommandEventWrapper wrapper, CurrentPlayEmbedContent c) {
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(config.getLocaleText(c.playingPauseMessage()))
            .addField(config.getLocaleText(LocaleSet.TRACK_NAME_MESS) + ":", c.trackUrl(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.TRACK_ADDDED_BY_MESS) + ":", c.addedByTag(), true)
            .addField(StringUtils.EMPTY, c.playerPercentageTrack(), false)
            .addField(config.getLocaleText(c.playingVisualizationTrack()), c.timestampNowAndMax(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.CURRENT_TRACK_LEFT_TO_NEXT_MESS), c.leftToNextTrack(), true)
            .setThumbnail(c.thumbnailUrl())
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createHelpMessage(CommandEventWrapper wrapper, HelpEmbedContent c) {
        final String compilationHeader = config.getLocaleText(LocaleSet.HELP_INFO_COMPILATION_VERSION_MESS);
        final String availableCommandsHeader = config.getLocaleText(LocaleSet.HELP_INFO_COUNT_OF_AVAILABLE_COMMANDS_MESS);
        return new EmbedBuilder()
            .setAuthor(wrapper.getAuthorTag(), null, wrapper.getAuthorAvatarUrl())
            .setDescription(c.description())
            .addField(compilationHeader + ":", c.compilationVersion(), true)
            .addField(availableCommandsHeader + ":", c.availableCommandsCount(), true)
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createQueueInfoMessage(QueueEmbedContent c) {
        return new EmbedBuilder()
            .addField(config.getLocaleText(LocaleSet.ALL_TRACKS_IN_QUEUE_COUNT_MESS) + ":", c.queueSize(), true)
            .addBlankField(true)
            .addField(config.getLocaleText(LocaleSet.ALL_TRACKS_IN_QUEUE_DURATION_MESS) + ":", content.queueMaxDuration(), true)
            .addField(config.getLocaleText(LocaleSet.APPROX_TO_NEXT_TRACK_FROM_QUEUE_MESS) + ":", content.approxToNextTrack(), true)
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createMessage(LocaleSet localeSet, Map<String, Object> attributes) {
        return new EmbedBuilder()
            .setDescription(config.getLocaleText(localeSet, attributes))
            .setColor(EmbedColor.ANTIQUE_WHITE.getColor())
            .build();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public MessageEmbed createMessage(LocaleSet localeSet) {
        return createMessage(localeSet, Map.of());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private String parseBugTracker(BugTracker bugTracker) {
        final String projectVersion = config.getProjectVersion().replaceAll("\\.", "");
        return String.format("j%sb%s_exc%06d", Runtime.version().feature(), projectVersion, bugTracker.getId());
    }
}
