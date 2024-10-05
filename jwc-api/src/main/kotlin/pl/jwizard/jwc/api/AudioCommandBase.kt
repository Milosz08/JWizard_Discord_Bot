/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.api

import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.GuildVoiceState
import net.dv8tion.jda.api.entities.channel.ChannelType
import pl.jwizard.jwc.command.CommandBase
import pl.jwizard.jwc.command.CommandEnvironmentBean
import pl.jwizard.jwc.command.event.context.CommandContext
import pl.jwizard.jwc.core.jda.command.TFutureResponse
import pl.jwizard.jwc.core.property.BotListProperty
import pl.jwizard.jwc.core.spi.lava.MusicManager
import pl.jwizard.jwc.exception.audio.TemporaryHaltedBotException
import pl.jwizard.jwc.exception.command.ForbiddenChannelException
import pl.jwizard.jwc.exception.command.InvokerIsNotSenderOrSuperuserException
import pl.jwizard.jwc.exception.user.UserOnVoiceChannelNotFoundException
import pl.jwizard.jwc.exception.user.UserOnVoiceChannelWithBotNotFoundException

/**
 * Base class for audio-related commands in the music command system.
 *
 * This class provides essential functionality for commands that interact with audio playback and requires a music
 * manager instance.
 *
 * @param commandEnvironment The environment context for executing the command.
 * @author Miłosz Gilga
 */
abstract class AudioCommandBase(commandEnvironment: CommandEnvironmentBean) : CommandBase(commandEnvironment) {

	/**
	 * Executes the audio command.
	 *
	 * This method performs various checks, including permissions and voice state, before delegating the execution to
	 * the concrete implementation of the audio command.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param response The future response object used to send the result of the command execution.
	 */
	final override fun execute(context: CommandContext, response: TFutureResponse) {
		val musicManager = commandEnvironment.musicManagersBean
			.getOrCreateMusicManager(context, commandEnvironment.lavalinkClientBean)

		val musicTextChannel = context.musicTextChannelId?.let { context.getTextChannel(it) }
		val musicTextChannelId = musicTextChannel?.idLong

		// check invoking channel id
		if (musicTextChannelId != null && context.textChannelId != musicTextChannelId) {
			val forbiddenChannel = context.getTextChannel(context.textChannelId)
			throw ForbiddenChannelException(context, forbiddenChannel, musicTextChannel)
		}
		// check, if bot (self member) is not currently muted
		if (context.selfMember?.voiceState?.isMuted == true) {
			throw TemporaryHaltedBotException(context)
		}
		// check, if content sender is sender or superuser
		if (shouldBeContentSenderOrSuperuser) {
			val (isSender, isDj, isSuperUser) = checkPermissions(context, musicManager)
			if (!isSender && !isDj && !isSuperUser) {
				throw InvokerIsNotSenderOrSuperuserException(context)
			}
		}
		executeAudio(context, musicManager, response)
	}

	/**
	 * Checks the permissions of the user in the context of the command.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The music manager responsible for handling the audio queue and playback.
	 * @return A Triple indicating whether the user is the sender, a DJ, or a superuser.
	 */
	protected fun checkPermissions(context: CommandContext, manager: MusicManager): Triple<Boolean, Boolean, Boolean> {
		val isSender = manager.getAudioSenderId(manager.cachedPlayer?.track) == context.authorId
		val isSuperUser = context.checkIfUserHasPermissions(*(superuserPermissions.toTypedArray()))
		val isDj = context.checkIfUserHasRoles(context.djRoleName)
		return Triple(isSender, isDj, isSuperUser)
	}

	/**
	 * Checks the user's voice state to ensure they are in a voice channel.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @return The voice state of the user in the guild.
	 * @throws UserOnVoiceChannelNotFoundException if the user is not in a voice channel.
	 * @throws ForbiddenChannelException if the user is in the AFK channel.
	 */
	protected fun checkUserVoiceState(context: CommandContext): GuildVoiceState {
		val userVoiceState = context.member?.voiceState
		if (userVoiceState?.channel?.type != ChannelType.VOICE) {
			throw UserOnVoiceChannelNotFoundException(context)
		}
		val afkChannel = context.guild?.afkChannel
		if (userVoiceState.channel == afkChannel) {
			val acceptedChannel = context.getTextChannel(context.textChannelId)
			throw ForbiddenChannelException(context, afkChannel, acceptedChannel)
		}
		return userVoiceState
	}

	/**
	 * Checks if the user is in the same voice channel as the bot.
	 *
	 * @param voiceState The voice state of the user.
	 * @param context The context of the command, containing user interaction details.
	 * @return Boolean indicating whether the user is with the bot in the same channel.
	 * @throws UserOnVoiceChannelWithBotNotFoundException if the user is not in the same channel as the bot.
	 */
	protected fun userIsWithBotOnAudioChannel(voiceState: GuildVoiceState, context: CommandContext): Boolean {
		val botVoiceState = context.selfMember?.voiceState
		// bot not yet joined to any channel, join to channel with invoker
		if (shouldAutoJoinBotToChannel && botVoiceState?.member?.voiceState?.inAudioChannel() == false) {
			return true
		}
		val superuserPermissions = environmentBean.getListProperty<String>(BotListProperty.JDA_SUPERUSER_PERMISSIONS)
		val isRegularUser = superuserPermissions.none { context.member?.hasPermission(Permission.valueOf(it)) == true }

		// check, if regular user is on the same channel with bot (omit for admin and server moderator)
		if (shouldOnSameChannelWithBot && botVoiceState?.channel?.id != voiceState.channel?.id && isRegularUser) {
			throw UserOnVoiceChannelWithBotNotFoundException(context, voiceState.channel, botVoiceState?.channel)
		}
		return false
	}

	/**
	 * Joins the user's voice channel if the bot is not already connected.
	 *
	 * @param context The context of the command, containing user interaction details.
	 */
	protected fun joinAndOpenAudioConnection(context: CommandContext) {
		if (context.selfMember?.voiceState?.inAudioChannel() == false) {
			context.member?.voiceState?.channel?.let { jdaInstance.directAudioController.connect(it) }
		}
	}

	/**
	 * Flag indicating whether the command requires the user to be in the same channel as the bot.
	 */
	protected open val shouldOnSameChannelWithBot = false

	/**
	 * Flag indicating whether the bot should automatically join the user's voice channel.
	 */
	protected open val shouldAutoJoinBotToChannel = false

	/**
	 * Flag indicating whether the command requires the content sender to be the sender or a superuser.
	 */
	protected open val shouldBeContentSenderOrSuperuser = false

	/**
	 * Executes the audio command.
	 *
	 * This method must be implemented by subclasses to define the specific functionality of the audio command.
	 *
	 * @param context The context of the command, containing user interaction details.
	 * @param manager The music manager responsible for handling the audio queue and playback.
	 * @param response The future response object used to send the result of the command execution.
	 */
	protected abstract fun executeAudio(context: CommandContext, manager: MusicManager, response: TFutureResponse)
}