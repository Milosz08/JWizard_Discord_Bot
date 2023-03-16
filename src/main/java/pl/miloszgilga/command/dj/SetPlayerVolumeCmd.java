/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: SetPlayerVolumeCmd.java
 * Last modified: 14/03/2023, 05:08
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

package pl.miloszgilga.command.dj;

import net.dv8tion.jda.api.entities.MessageEmbed;
import org.apache.commons.lang3.math.NumberUtils;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.Map;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.EventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.command.AbstractDjCommand;
import pl.miloszgilga.core.LocaleSet;
import pl.miloszgilga.core.configuration.BotProperty;
import pl.miloszgilga.core.configuration.BotConfiguration;
import pl.miloszgilga.core.loader.JDAInjectableCommandLazyService;

import static pl.miloszgilga.exception.AudioPlayerException.VolumeUnitsOutOfBoundsException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

@JDAInjectableCommandLazyService
public class SetPlayerVolumeCmd extends AbstractDjCommand {

    SetPlayerVolumeCmd(BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder) {
        super(BotCommand.SET_PLAYER_VOLUME, config, playerManager, embedBuilder);
        super.inPlayingMode = false;
        super.inListeningMode = false;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteDjCommand(CommandEvent event) {
        final short defaultVolumeUnits = config.getProperty(BotProperty.J_DEFAULT_PLAYER_VOLUME_UNITS, Short.class);
        final short currentVolumeUnits = playerManager.getMusicManager(event).getPlayerVolume();
        final short volumeUnits = NumberUtils.toShort(event.getArgs(), defaultVolumeUnits);
        if (volumeUnits < 0 || volumeUnits > 150) {
            throw new VolumeUnitsOutOfBoundsException(config, new EventWrapper(event));
        }
        playerManager.setPlayerVolume(event, currentVolumeUnits);
        final MessageEmbed messageEmbed = embedBuilder
            .createMessage(LocaleSet.SET_CURRENT_AUDIO_PLAYER_VOLUME_MESS, Map.of(
                "previousVolume", currentVolumeUnits,
                "setVolume", volumeUnits
            ));
        event.getTextChannel().sendMessageEmbeds(messageEmbed).queue();
    }
}