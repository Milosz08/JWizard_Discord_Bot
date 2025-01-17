/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.command

import net.dv8tion.jda.api.entities.channel.Channel
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.core.util.ext.qualifier
import pl.jwizard.jwc.exception.CommandPipelineException
import pl.jwizard.jwl.i18n.source.I18nExceptionSource

/**
 * Exception thrown when a command is attempted in a forbidden channel that does not allow the execution of that command.
 *
 * @param context The context of the command that caused the exception.
 * @param forbiddenChannel The channel where the command was attempted but not allowed.
 * @param acceptedChannel The channel where the command is accepted.
 * @author Miłosz Gilga
 */
class ForbiddenChannelException(
	context: CommandBaseContext,
	forbiddenChannel: Channel?,
	acceptedChannel: Channel?,
) : CommandPipelineException(
	commandBaseContext = context,
	i18nExceptionSource = I18nExceptionSource.FORBIDDEN_CHANNEL,
	args = mapOf("acceptChannel" to acceptedChannel?.name),
	logMessage = """
		Attempt to use song request command on forbidden channel: ${forbiddenChannel?.qualifier}.
		Accepted channel: ${acceptedChannel?.qualifier}.
	""",
)
