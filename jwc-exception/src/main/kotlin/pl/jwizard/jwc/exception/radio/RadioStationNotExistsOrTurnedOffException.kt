/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.exception.radio

import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.jda.command.CommandBaseContext
import pl.jwizard.jwc.exception.CommandPipelineExceptionHandler

/**
 * Exception thrown when an attempt is made to interact with a radio station that either does not exist or is turned
 * off.
 *
 * @param commandBaseContext The context of the command that caused the exception.
 * @param radioStationSlug The identifier of the radio station that was attempted.
 * @author Miłosz Gilga
 */
class RadioStationNotExistsOrTurnedOffException(
	commandBaseContext: CommandBaseContext,
	radioStationSlug: String,
) : CommandPipelineExceptionHandler(
	commandBaseContext,
	i18nExceptionSource = I18nExceptionSource.RADIO_STATION_NOT_EXISTS_IS_TURNED_OFF,
	logMessage = "Attempt to invoke command, while radio station: \"$radioStationSlug\" is turned off."
)