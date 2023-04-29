/*
 * Copyright (c) 2023 by MILOSZ GILGA <http://miloszgilga.pl>
 *
 * File name: AbstractVoteMusicCommand.java
 * Last modified: 19/03/2023, 16:51
 * Project name: jwizard-discord-bot
 *
 * Licensed under the MIT license; you may not use this file except in compliance with the License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * THE ABOVE COPYRIGHT NOTICE AND THIS PERMISSION NOTICE SHALL BE INCLUDED IN ALL COPIES OR
 * SUBSTANTIAL PORTIONS OF THE SOFTWARE.
 *
 * The software is provided "as is", without warranty of any kind, express or implied, including but not limited
 * to the warranties of merchantability, fitness for a particular purpose and noninfringement. In no event
 * shall the authors or copyright holders be liable for any claim, damages or other liability, whether in an
 * action of contract, tort or otherwise, arising from, out of or in connection with the software or the use
 * or other dealings in the software.
 */

package pl.miloszgilga.command;

import pl.miloszgilga.BotCommand;
import pl.miloszgilga.dto.CommandEventWrapper;
import pl.miloszgilga.vote.VoteEmbedResponse;
import pl.miloszgilga.vote.VotingSystemSequencer;
import pl.miloszgilga.audioplayer.PlayerManager;
import pl.miloszgilga.embed.EmbedMessageBuilder;
import pl.miloszgilga.core.remote.RemoteModuleProperty;
import pl.miloszgilga.core.remote.RemotePropertyHandler;
import pl.miloszgilga.core.configuration.BotConfiguration;

import static pl.miloszgilga.exception.ModuleException.VotingModuleIsTurnedOffException;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

public abstract class AbstractVoteMusicCommand extends AbstractMusicCommand {

    protected AbstractVoteMusicCommand(
        BotCommand command, BotConfiguration config, PlayerManager playerManager, EmbedMessageBuilder embedBuilder,
        RemotePropertyHandler handler
    ) {
        super(command, config, playerManager, embedBuilder, handler);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    protected void doExecuteMusicCommand(CommandEventWrapper event) {
        if (!handler.getPossibleRemoteModuleProperty(RemoteModuleProperty.R_VOTING_MODULE_ENABLED, event.getGuild())) {
            throw new VotingModuleIsTurnedOffException(config, event);
        }
        final VoteEmbedResponse response = doExecuteVoteMusicCommand(event);
        final VotingSystemSequencer votingSystemSequencer = new VotingSystemSequencer(response, event, config);
        votingSystemSequencer.initializeAndStartVoting();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected abstract VoteEmbedResponse doExecuteVoteMusicCommand(CommandEventWrapper event);
}
