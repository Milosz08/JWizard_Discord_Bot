/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractDjCommand.java
 * Last modified: 14/03/2023, 05:44
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

package pl.miloszgilga.command;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.misc.Utilities;
import pl.miloszgilga.misc.ValidateUserDetails;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.CommandException.UnauthorizedDjCommandExecutionException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractDjCommand extends AbstractMusicCommand {

    protected AbstractDjCommand(
        BotCommand command, BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder
    ) {
        super(command, config, playerManager, embedBuilder);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        final ValidateUserDetails details = Utilities.validateUserDetails(event, config);
        if (details.isNotOwner() && details.isNotManager() && details.isNotDj()) {
            throw new UnauthorizedDjCommandExecutionException(config, event);
        }
        doExecuteDjCommand(event);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract void doExecuteDjCommand(CommandEventWrapper event);
}
