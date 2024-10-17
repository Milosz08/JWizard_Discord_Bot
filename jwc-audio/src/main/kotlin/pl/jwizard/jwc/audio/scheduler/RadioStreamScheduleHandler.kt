/*
 * Copyright (c) 2024 by JWizard
 * Originally developed by Miłosz Gilga <https://miloszgilga.pl>
 */
package pl.jwizard.jwc.audio.scheduler

import dev.arbjerg.lavalink.client.LavalinkNode
import dev.arbjerg.lavalink.client.player.Track
import dev.arbjerg.lavalink.client.player.TrackException
import dev.arbjerg.lavalink.protocol.v4.Message.EmittedEvent.TrackEndEvent.AudioTrackEndReason
import pl.jwizard.jwc.audio.RadioStationDetails
import pl.jwizard.jwc.audio.manager.GuildMusicManager
import pl.jwizard.jwc.command.refer.Command
import pl.jwizard.jwc.core.audio.spi.RadioStreamScheduler
import pl.jwizard.jwc.core.i18n.source.I18nDynamicMod
import pl.jwizard.jwc.core.i18n.source.I18nExceptionSource
import pl.jwizard.jwc.core.i18n.source.I18nResponseSource
import pl.jwizard.jwc.core.jda.color.JdaColor
import pl.jwizard.jwc.core.jda.command.CommandResponse
import pl.jwizard.jwc.core.util.jdaInfo
import pl.jwizard.jwc.core.util.logger

/**
 * Handles the scheduling and management of radio streams in a Discord bot context.
 *
 * This class is responsible for loading radio streams, managing their playback, and responding to events such as
 * starting, stopping, and error handling.
 *
 * @property musicManager The [GuildMusicManager] instance used for managing audio playback.
 * @property radioStationDetails Details about the radio station, including its name and stream URL.
 * @author Miłosz Gilga
 */
class RadioStreamScheduleHandler(
	private val musicManager: GuildMusicManager,
	private val radioStationDetails: RadioStationDetails,
) : AudioScheduleHandler(musicManager), RadioStreamScheduler {

	companion object {
		private val log = logger<RadioStreamScheduleHandler>()
	}

	override val radioSlug get() = radioStationDetails.name

	/**
	 * Loads the specified list of tracks into the queue. For radio streams, this method simply starts the first track in
	 * the provided list if it is not empty.
	 *
	 * @param tracks The list of [Track]s to be loaded into the queue. Typically only the first track is used for radio.
	 */
	override fun loadContent(tracks: List<Track>) {
		if (tracks.isNotEmpty()) {
			startTrack(tracks[0])
		}
	}

	/**
	 * Handles the event when an audio track starts playing. Sends a message to the context indicating that the radio
	 * station has started playing.
	 *
	 * @param track The [Track] that has started playing.
	 * @param node The [LavalinkNode] on which the track is playing.
	 */
	override fun onAudioStart(track: Track, node: LavalinkNode) {
		val state = musicManager.state
		val context = state.context
		val i18nBean = musicManager.beans.i18nBean

		val radioStationName = i18nBean.tRaw(
			i18nDynamicMod = I18nDynamicMod.ARG_OPTION_MOD,
			args = arrayOf("radio", radioStationDetails.name),
			lang = context.guildLanguage,
		)
		val listElements = mapOf(
			I18nResponseSource.START_PLAYING_RADIO_STATION_FIRST_OPTION to mapOf("stopRadioStationCmd" to Command.STOP_RADIO),
			I18nResponseSource.START_PLAYING_RADIO_STATION_SECOND_OPTION to mapOf("radioStationInfoCmd" to Command.RADIO_INFO),
		)
		val parsedListElements = listElements.entries.joinToString("\n") { (i18nKey, i18nArgs) ->
			"* ${i18nBean.t(i18nKey, context.guildLanguage, i18nArgs.mapValues { it.value.parseWithPrefix(context) })}"
		}
		val message = musicManager.createEmbedBuilder()
			.setTitle(
				i18nLocaleSource = I18nResponseSource.START_PLAYING_RADIO_STATION,
				args = mapOf("radioStationName" to radioStationName),
			)
			.setDescription(parsedListElements)
			.setArtwork(musicManager.beans.radioStationThumbnailSupplier.getThumbnailUrl(radioStationDetails.name))
			.setColor(JdaColor.PRIMARY)
			.build()

		log.jdaInfo(
			state.context,
			"Node: %s. Start playing radio station: %s from stream URL: %s.",
			node.name,
			radioStationDetails.name,
			radioStationDetails.streamUrl,
		)
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()
		state.future.complete(response)
	}

	/**
	 * Handles the event when an audio track ends.
	 *
	 * Sends a message to the context indicating that the radio station has stopped playing. Offers the option to start
	 * playing the radio station again.
	 *
	 * @param lastTrack The [Track] that just finished playing.
	 * @param node The [LavalinkNode] on which the track was playing.
	 * @param endReason The reason for the track ending.
	 */
	override fun onAudioEnd(lastTrack: Track, node: LavalinkNode, endReason: AudioTrackEndReason) {
		val state = musicManager.state
		val context = state.context
		val supplier = musicManager.beans.radioStationThumbnailSupplier

		val message = musicManager.createEmbedBuilder()
			.setDescription(
				i18nLocaleSource = I18nResponseSource.STOP_PLAYING_RADIO_STATION,
				args = mapOf(
					"radioStationName" to radioStationDetails.name,
					"startRadioStationCmd" to Command.PLAY_RADIO.parseWithPrefix(context),
				),
			)
			.setColor(JdaColor.PRIMARY)
			.setArtwork(supplier.getThumbnailUrl(radioStationDetails.name))
			.build()

		musicManager.startLeavingWaiter()

		log.jdaInfo(context, "Node: %s. Stop playing radio station: %s.", node.name, radioStationDetails.name)
		val response = CommandResponse.Builder()
			.addEmbedMessages(message)
			.build()
		state.future.complete(response)
	}

	/**
	 * Handles the event when an audio track gets stuck during playback. Calls the [onError] method to manage the error
	 * handling process.
	 *
	 * @param track The [Track] that is stuck.
	 * @param node The [LavalinkNode] on which the track was playing.
	 */
	override fun onAudioStuck(track: Track, node: LavalinkNode) = onError(node, "Radio stuck.")

	/**
	 * Handles the event when an error occurs while playing an audio track. Logs the error and sends an appropriate
	 * message to the context.
	 *
	 * @param track The [Track] that encountered an error.
	 * @param node The [LavalinkNode] on which the error occurred.
	 * @param exception The [TrackException] that contains the error details.
	 */
	override fun onAudioException(track: Track, node: LavalinkNode, exception: TrackException) =
		onError(node, exception.message)

	/**
	 * Handles errors that occur during radio stream playback. Logs the error and sends a message with details about
	 * the issue.
	 *
	 * @param node The [LavalinkNode] on which the error occurred.
	 * @param logMessage A message explaining the cause of the error.
	 */
	private fun onError(node: LavalinkNode, logMessage: String?) {
		val context = musicManager.state.context
		val i18nBean = musicManager.beans.i18nBean
		val tracker = musicManager.beans.exceptionTrackerStore

		val radioStationName = i18nBean.tRaw(
			i18nDynamicMod = I18nDynamicMod.ARG_OPTION_MOD,
			args = arrayOf("radio", radioStationDetails.name),
			lang = context.guildLanguage,
		)
		musicManager.state.audioScheduler.stopAndDestroy()
		musicManager.startLeavingWaiter()

		val i18nLocaleSource = I18nExceptionSource.UNEXPECTED_ERROR_WHILE_STREAMING_RADIO
		val message = tracker.createTrackerMessage(i18nLocaleSource, context, mapOf("radioStation" to radioStationName))
		val link = tracker.createTrackerLink(i18nLocaleSource, context)
		log.jdaInfo(
			context,
			"Node: %s. Unexpected error while streaming radio: %s. Cause: %s.",
			node.name,
			radioStationDetails.name,
			logMessage
		)
		musicManager.sendMessage(message, link)
	}
}